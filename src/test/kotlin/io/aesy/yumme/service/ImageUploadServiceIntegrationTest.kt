package io.aesy.yumme.service

import com.ninjasquad.springmockk.MockkBean
import io.aesy.test.TestType
import io.aesy.test.extension.MariaDBExtension
import io.aesy.yumme.storage.InMemoryStorage
import io.aesy.test.util.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.repository.ImageUploadRepository
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import strikt.api.expectThat
import strikt.java.isPresent
import java.util.*
import javax.imageio.ImageIO

@TestType.Integration
@ExtendWith(MariaDBExtension::class)
@ContextConfiguration(
    classes = [
        ImageUploadService::class,
        InMemoryStorage::class
    ]
)
class ImageUploadServiceIntegrationTest {
    @Autowired
    private lateinit var uploadService: ImageUploadService

    @MockkBean
    private lateinit var imageUploadRepository: ImageUploadRepository

    @MockkBean
    private lateinit var recipeHasImageUploadRepository: RecipeHasImageUploadRepository

    @Test
    fun `It should be possible to store an image and get the original back by name`() {
        val author = Users.random()
        val recipe = Recipes.random(author)
        val image = Resources.open("/testData/small_image.png").use(ImageIO::read)

        every { imageUploadRepository.save(any()) } returnsArgument 0
        every { recipeHasImageUploadRepository.save(any()) } returnsArgument 0

        val upload = uploadService.storeImage(recipe, image)

        verify { imageUploadRepository.save(any()) }
        verify { recipeHasImageUploadRepository.save(any()) }
        every {
            recipeHasImageUploadRepository.findByRecipeAndNameAndType(recipe, upload.name, Type.ORIGINAL)
        } returns Optional.of(upload)

        val result = uploadService.getUpload(recipe, upload.name, Type.ORIGINAL)

        expectThat(result).isPresent()
    }
}
