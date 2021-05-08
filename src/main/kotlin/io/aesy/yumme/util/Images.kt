package io.aesy.yumme.util

import java.awt.*
import java.awt.RenderingHints.KEY_RENDERING
import java.awt.RenderingHints.VALUE_RENDER_QUALITY
import java.awt.image.BufferedImage

object Images {
    fun removeTransparency(image: BufferedImage): BufferedImage {
        return BufferedImage(image.width, image.height, image.type).apply {
            val graphics = createGraphics()
            graphics.composite = AlphaComposite.DstOver
            graphics.paint = Color.WHITE
            graphics.fillRect(0, 0, width, height)
            graphics.dispose()
        }
    }

    fun rescaleToFit(image: BufferedImage, boundary: Dimension): BufferedImage {
        var size = Dimension(image.width, image.height)

        if (size.width > boundary.width) {
            size = Dimension(boundary.width, boundary.width * size.height / size.width)
        }

        if (size.height > boundary.height) {
            size = Dimension(boundary.height * size.width / size.height, boundary.height)
        }

        return BufferedImage(size.width, size.height, image.type).apply {
            val graphics = createGraphics()
            graphics.addRenderingHints(RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY))
            graphics.drawImage(image, 0, 0, size.width, size.height, null)
            graphics.dispose()
        }
    }
}
