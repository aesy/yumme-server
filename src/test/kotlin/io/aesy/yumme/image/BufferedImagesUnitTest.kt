package io.aesy.yumme.image

import io.aesy.test.TestType
import io.aesy.yumme.image.BufferedImages.crop
import io.aesy.yumme.image.BufferedImages.rescale
import io.aesy.yumme.image.BufferedImages.rescaleToCover
import io.aesy.yumme.image.BufferedImages.rescaleToFit
import io.aesy.yumme.image.BufferedImages.resize
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.awt.Dimension
import java.awt.image.BufferedImage

@TestType.Unit
class BufferedImagesUnitTest {
    @Test
    fun `It should be able to crop an image`() {
        val image = BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        val cropped = image.crop(Dimension(100, 200))

        expectThat(cropped.width).isEqualTo(100)
        expectThat(cropped.height).isEqualTo(200)
    }

    @Test
    fun `It should be able to resize an image`() {
        val image = BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        val resized = image.resize(Dimension(200, 100))

        expectThat(resized.width).isEqualTo(200)
        expectThat(resized.height).isEqualTo(100)
    }

    @Test
    fun `It should be able to rescale an image by some percent`() {
        val image = BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        var rescaled: BufferedImage

        // Scale down
        rescaled = image.rescale(0.5)

        expectThat(rescaled.width).isEqualTo(image.width / 2)
        expectThat(rescaled.height).isEqualTo(image.height / 2)

        // Scale up
        rescaled = image.rescale(3.0)

        expectThat(rescaled.width).isEqualTo(image.width * 3)
        expectThat(rescaled.height).isEqualTo(image.height * 3)
    }

    @Test
    fun `It should be able to rescale an image to fit some dimensions`() {
        var image: BufferedImage
        var rescaled: BufferedImage

        // Scale down to fit height
        image = BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(200, 100))

        expectThat(rescaled.width).isEqualTo(150)
        expectThat(rescaled.height).isEqualTo(100)

        // Scale down square to fit height
        image = BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(200, 100))

        expectThat(rescaled.width).isEqualTo(100)
        expectThat(rescaled.height).isEqualTo(100)

        // Scale down to fit width
        image = BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(100, 100))

        expectThat(rescaled.width).isEqualTo(100)
        expectThat(rescaled.height).isEqualTo(50)

        // Scale already fits
        image = BufferedImage(50, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(100, 200))

        expectThat(rescaled.width).isEqualTo(50)
        expectThat(rescaled.height).isEqualTo(200)

        // Scale same size
        image = BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(100, 200))

        expectThat(rescaled.width).isEqualTo(100)
        expectThat(rescaled.height).isEqualTo(200)

        // Scale up to fit width
        image = BufferedImage(50, 20, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToFit(Dimension(100, 100))

        expectThat(rescaled.width).isEqualTo(100)
        expectThat(rescaled.height).isEqualTo(40)
    }

    @Test
    fun `It should be able to rescale an image to cover some dimensions`() {
        var image: BufferedImage
        var rescaled: BufferedImage

        // Scale down to cover width
        image = BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(200, 100))

        expectThat(rescaled.width).isEqualTo(200)
        expectThat(rescaled.height).isEqualTo(150)

        // Scale down square to cover width
        image = BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(200, 100))

        expectThat(rescaled.width).isEqualTo(200)
        expectThat(rescaled.height).isEqualTo(200)

        // Scale down to cover height
        image = BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(100, 100))

        expectThat(rescaled.width).isEqualTo(200)
        expectThat(rescaled.height).isEqualTo(100)

        // Scale already fits
        image = BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(100, 200))

        expectThat(rescaled.width).isEqualTo(400)
        expectThat(rescaled.height).isEqualTo(200)

        // Scale same size
        image = BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(100, 200))

        expectThat(rescaled.width).isEqualTo(100)
        expectThat(rescaled.height).isEqualTo(200)

        // Scale up to cover height
        image = BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB)
        rescaled = image.rescaleToCover(Dimension(100, 100))

        expectThat(rescaled.width).isEqualTo(200)
        expectThat(rescaled.height).isEqualTo(100)
    }
}
