package io.aesy.yumme.storage

import com.amazonaws.SdkClientException
import com.amazonaws.auth.*
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.regions.DefaultAwsRegionProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.S3ObjectSummary
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.*

@Service
@ConditionalOnProperty("yumme.storage.type", havingValue = "s3")
class S3Storage(
    @Value("\${yumme.storage.path}")
    private val path: String,
    @Value("\${aws.s3.bucket}")
    private val bucket: String,
    @Value("\${aws.s3.endpoint}")
    endpoint: String?,
    @Value("\${aws.s3.access-key}")
    accessKey: String?,
    @Value("\${aws.s3.secret-key}")
    secretKey: String?,
    @Value("\${aws.s3.region}")
    region: String?
): Storage {
    private val s3: AmazonS3

    init {
        val builder = AmazonS3ClientBuilder.standard()

        if (accessKey != null && secretKey != null) {
            val credentials = BasicAWSCredentials(accessKey, secretKey)
            builder.withCredentials(AWSStaticCredentialsProvider(credentials))
        } else {
            builder.withCredentials(DefaultAWSCredentialsProviderChain())
        }

        if (endpoint != null) {
            val signingRegion = region ?: DefaultAwsRegionProviderChain().region ?: Regions.US_EAST_1.name

            builder.withEndpointConfiguration(EndpointConfiguration(endpoint, signingRegion))
        } else if (region != null) {
            builder.withRegion(region)
        } else {
            builder.withForceGlobalBucketAccessEnabled(true)
        }

        s3 = builder.build()
    }

    @Throws(IOException::class)
    override fun read(filename: String): InputStream {
        try {
            return s3.getObject(bucket, path + filename).objectContent
        } catch (e: SdkClientException) {
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    override fun listFiles(): List<String> {
        try {
            return s3.listObjects(bucket, path)
                .objectSummaries
                .map(S3ObjectSummary::getKey)
                .toList()
        } catch (e: SdkClientException) {
            throw IOException(e)
        }
    }

    @Throws(IOException::class)
    override fun write(filename: String, bytes: ByteArray) {
        val metadata = ObjectMetadata()
        metadata.contentLength = bytes.size.toLong()

        ByteArrayInputStream(bytes).use {
            try {
                s3.putObject(bucket, path + filename, it, metadata)
            } catch (e: SdkClientException) {
                throw IOException(e)
            }
        }
    }

    @Throws(IOException::class)
    override fun delete(filename: String): Boolean {
        try {
            s3.deleteObject(bucket, path + filename)
        } catch (e: SdkClientException) {
            throw IOException(e)
        }

        return true
    }
}
