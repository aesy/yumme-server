package io.aesy.yumme.service

import com.ninjasquad.springmockk.MockkBean
import io.aesy.test.TestType
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.repository.ImageUploadRepository
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.util.*
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import strikt.api.expectThat
import strikt.java.isPresent
import java.nio.file.Files
import java.util.*

@TestType.Unit
@SpringBootTest(
    classes = [
        ImageUploadService::class,
        FileStorageService::class,
        SlugService::class
    ]
)
class ImageUploadServiceUnitTest {
    @Autowired
    private lateinit var uploadService: ImageUploadService

    @MockkBean
    private lateinit var imageUploadRepository: ImageUploadRepository

    @MockkBean
    private lateinit var recipeHasImageUploadRepository: RecipeHasImageUploadRepository

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("yumme.storage.directory") {
                Files.createTempDirectory("YummeIntegrationTest").toString()
            }
        }
    }

    @Test
    fun `It should be possible to store an image and get the original back by name`() {
        val author = Users.random()
        val recipe = Recipes.random(author)
        val stream = Resources.open("/testData/small_image.png")

        every { imageUploadRepository.save(any()) } returnsArgument 0
        every { recipeHasImageUploadRepository.save(any()) } returnsArgument 0

        val upload = uploadService.storeImage(recipe, stream)

        verify { imageUploadRepository.save(any()) }
        verify { recipeHasImageUploadRepository.save(any()) }
        every {
            recipeHasImageUploadRepository.findByRecipeAndNameAndType(recipe, upload.name, Type.ORIGINAL)
        } returns Optional.of(upload)

        val image = uploadService.getUpload(recipe, upload.name, Type.ORIGINAL)

        expectThat(image).isPresent()
    }
}
