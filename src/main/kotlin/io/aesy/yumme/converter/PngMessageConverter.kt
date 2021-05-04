package io.aesy.yumme.converter

import org.apache.commons.imaging.*
import org.springframework.http.*
import org.springframework.http.converter.*
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.IOException

@Component
class PngMessageConverter: AbstractHttpMessageConverter<BufferedImage>(MediaType.IMAGE_PNG) {
    override fun supports(clazz: Class<*>): Boolean {
        return BufferedImage::class.java.isAssignableFrom(clazz)
    }

    @Throws(HttpMessageNotReadableException::class)
    override fun readInternal(
        clazz: Class<out BufferedImage>,
        input: HttpInputMessage
    ): BufferedImage {
        return try {
            Imaging.getBufferedImage(input.body)
        } catch (exception: ImageReadException) {
            throw HttpMessageNotReadableException("Failed to decode image", exception, input)
        } catch (exception: IOException) {
            throw HttpMessageNotReadableException("Failed to decode image", exception, input)
        }
    }

    @Throws(HttpMessageNotWritableException::class)
    override fun writeInternal(
        image: BufferedImage,
        outputMessage: HttpOutputMessage
    ) {
        try {
            Imaging.writeImage(image, outputMessage.body, ImageFormats.PNG, mutableMapOf())
        } catch (exception: ImageWriteException) {
            throw HttpMessageNotWritableException("Failed to encode image", exception)
        } catch (exception: IOException) {
            throw HttpMessageNotWritableException("Failed to encode image", exception)
        }
    }
}
