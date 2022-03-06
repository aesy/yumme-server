package io.aesy.yumme.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

@Service
@ConditionalOnProperty("yumme.storage.type", havingValue = "file")
class LocalFileStorage(
    @Value("\${yumme.storage.path}")
    private val directory: Path
): Storage {
    init {
        if (!Files.isDirectory(directory)) {
            Files.createDirectories(directory)
        }
    }

    @Throws(IOException::class)
    override fun read(filename: String): InputStream {
        return Files.newInputStream(directory.resolve(filename))
    }

    @Throws(IOException::class)
    override fun listFiles(): List<String> {
        Files.list(directory).use {
            return it
                .filter(Files::isRegularFile)
                .map(Path::name)
                .toList()
        }
    }

    @Throws(IOException::class)
    override fun write(filename: String, bytes: ByteArray) {
        val filePath = directory.resolve(filename)

        Files.write(filePath, bytes)
    }

    @Throws(IOException::class)
    override fun delete(filename: String): Boolean {
        val path = directory.resolve(filename)

        return Files.deleteIfExists(path)
    }
}
