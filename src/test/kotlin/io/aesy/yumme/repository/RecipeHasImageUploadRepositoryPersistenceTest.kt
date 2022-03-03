package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.util.Strings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isPresent

@TestType.Persistence
class RecipeHasImageUploadRepositoryPersistenceTest {
    @Autowired
    private lateinit var recipeHasImageUploadRepository: RecipeHasImageUploadRepository

    @Autowired
    private lateinit var imageUploadRepository: ImageUploadRepository

    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist an image upload`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val upload = imageUploadRepository.save(createUpload())
        val mapping = recipeHasImageUploadRepository.save(createOriginalMapping(upload, recipe))

        expectThat(mapping.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch an image uploads by recipe and name`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val upload1 = imageUploadRepository.save(createUpload())
        val mapping1 = recipeHasImageUploadRepository.save(createOriginalMapping(upload1, recipe))
        val upload2 = imageUploadRepository.save(createUpload())
        val mapping2 = recipeHasImageUploadRepository.save(
            RecipeHasImageUpload(
                name = mapping1.name,
                type = Type.THUMBNAIL,
                recipe = recipe,
                upload = upload2
            )
        )

        val uploads = recipeHasImageUploadRepository.findByRecipeAndName(recipe, mapping1.name)

        expectThat(uploads)
            .map(RecipeHasImageUpload::id)
            .containsExactly(mapping1.id, mapping2.id)
    }

    @Test
    fun `It should be possible to fetch an image uploads by recipe and type`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val upload1 = imageUploadRepository.save(createUpload())
        val mapping1 = recipeHasImageUploadRepository.save(createOriginalMapping(upload1, recipe))
        val upload2 = imageUploadRepository.save(createUpload())
        recipeHasImageUploadRepository.save(
            RecipeHasImageUpload(
                name = mapping1.name,
                type = Type.THUMBNAIL,
                recipe = recipe,
                upload = upload2
            )
        )

        val uploads = recipeHasImageUploadRepository.findByRecipeAndType(recipe, Type.ORIGINAL)

        expectThat(uploads)
            .map(RecipeHasImageUpload::id)
            .containsExactly(mapping1.id)
    }

    @Test
    fun `It should be possible to fetch an image upload by recipe, name and type`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val upload1 = imageUploadRepository.save(createUpload())
        val mapping1 = recipeHasImageUploadRepository.save(createOriginalMapping(upload1, recipe))
        val upload2 = imageUploadRepository.save(createUpload())
        recipeHasImageUploadRepository.save(createOriginalMapping(upload2, recipe))

        val uploads = recipeHasImageUploadRepository.findByRecipeAndNameAndType(recipe, mapping1.name, mapping1.type)

        expectThat(uploads)
            .isPresent()
            .get { expectThat(id).isEqualTo(upload1.id) }
    }

    @Test
    fun `It should be possible to fetch all original images without thumbnails`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val upload1 = imageUploadRepository.save(createUpload())
        val mapping1 = recipeHasImageUploadRepository.save(createOriginalMapping(upload1, recipe))
        val upload2 = imageUploadRepository.save(createUpload())
        recipeHasImageUploadRepository.save(
            RecipeHasImageUpload(
                name = Strings.random(30),
                type = Type.THUMBNAIL,
                recipe = recipe,
                upload = upload2
            )
        )

        val uploads = recipeHasImageUploadRepository.findUnprocessedOriginalsByType(Type.THUMBNAIL.name)

        expectThat(uploads)
            .map(RecipeHasImageUpload::id)
            .containsExactly(mapping1.id)
    }

    private fun createUpload(): ImageUpload {
        return ImageUpload(
            fileName = Strings.random(30),
            hash = Strings.random(32),
            width = 0,
            height = 0
        )
    }

    private fun createOriginalMapping(upload: ImageUpload, recipe: Recipe): RecipeHasImageUpload {
        return RecipeHasImageUpload(
            name = Strings.random(30),
            type = Type.ORIGINAL,
            recipe = recipe,
            upload = upload
        )
    }
}
