package io.aesy.yumme.image

import java.awt.*
import java.awt.RenderingHints.KEY_RENDERING
import java.awt.RenderingHints.VALUE_RENDER_QUALITY
import java.awt.image.BufferedImage
import java.awt.image.RasterFormatException
import kotlin.math.*

object BufferedImages {
    fun BufferedImage.removeTransparency(): BufferedImage {
        return BufferedImage(width, height, type).apply {
            val graphics = createGraphics()
            graphics.composite = AlphaComposite.DstOver
            graphics.paint = Color.WHITE
            graphics.fillRect(0, 0, width, height)
            graphics.dispose()
        }
    }

    @Throws(RasterFormatException::class)
    fun BufferedImage.crop(
        size: Dimension,
        xAlign: Alignment = Alignment.START,
        yAlign: Alignment = Alignment.START
    ): BufferedImage {
        val x = when (xAlign) {
            Alignment.START -> 0
            Alignment.CENTER -> (width - size.width) / 2
            Alignment.END -> width - size.width
        }
        val y = when (yAlign) {
            Alignment.START -> 0
            Alignment.CENTER -> (height - size.height) / 2
            Alignment.END -> height - size.height
        }

        return getSubimage(x, y, size.width, size.height)
    }

    fun BufferedImage.resize(size: Dimension): BufferedImage {
        return BufferedImage(size.width, size.height, type).apply {
            val graphics = createGraphics()
            graphics.addRenderingHints(RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY))
            graphics.drawImage(this, 0, 0, size.width, size.height, null)
            graphics.dispose()
        }
    }

    fun BufferedImage.rescale(scale: Double): BufferedImage {
        if (scale < 0) {
            throw IllegalArgumentException("Scale must not be below zero")
        }

        val size = Dimension(
            (width * scale).roundToInt(),
            (height * scale).roundToInt()
        )

        return resize(size)
    }

    fun BufferedImage.rescaleToFit(size: Dimension): BufferedImage {
        val scale = min(
            size.width.toDouble() / width.toDouble(),
            size.height.toDouble() / height.toDouble()
        )

        return rescale(scale)
    }

    fun BufferedImage.rescaleToCover(size: Dimension): BufferedImage {
        val scale = max(
            size.width.toDouble() / width.toDouble(),
            size.height.toDouble() / height.toDouble()
        )

        return rescale(scale)
    }
}
