package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.*
import io.aesy.yumme.repository.ImageUploadRepository
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.service.*
import io.aesy.yumme.util.HTTP.getList
import io.aesy.yumme.util.Recipes
import io.aesy.yumme.util.Strings
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isAbsent
import strikt.java.isPresent
import java.time.Duration

@TestType.RestApi
class RecipeRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var ingredientService: IngredientService

    @Autowired
    private lateinit var ratingService: RatingService

    @Autowired
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var imageUploadRepository: ImageUploadRepository

    @Autowired
    private lateinit var recipeHasUploadRepository: RecipeHasImageUploadRepository

    @BeforeEach
    fun setup() {
        // For PATCH support...
        restTemplate.restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory()
    }

    @Test
    fun `It should be possible to list the authenticated users recipes`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = false
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author1.userName, "secret")
            .getList<RecipeDto>("/recipe")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe1.id, recipe2.id)
    }

    @Test
    fun `It should be possible to list recipes by user`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author2)
        recipe2.public = false
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author1.userName, "secret")
            .getList<RecipeDto>("/recipe?user=${author2.id}")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe3.id)
    }

    @Test
    fun `It should be possible to fetch the most recently created public recipes`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = false
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = true
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author2.userName, "secret")
            .getList<RecipeDto>("/recipe/recent")

        expectThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe3.id, recipe2.id)
    }

    @Test
    fun `It should be possible to fetch the most recently created public recipes by user`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = false
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = true
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author1)
        recipe3.public = true
        recipeService.save(recipe3)
        val recipe4 = Recipes.random(author2)
        recipe4.public = true
        recipeService.save(recipe4)

        val response = restTemplate.withBasicAuth(author2.userName, "secret")
            .getList<RecipeDto>("/recipe/recent?user=${author1.id}")

        expectThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe3.id, recipe2.id)
    }

    @Test
    fun `It should be possible to create a new recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        categoryService.save(Category(name = "abc"))
        val request = CreateRecipeRequest(
            "woop",
            "woop",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        ).apply {
            categories = mutableSetOf("abc")
            tags = mutableSetOf("def")
        }

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<RecipeDto>("/recipe", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val recipe = response.body

        expectThat(recipe).isNotNull()
        expectThat(recipe!!.id).isNotNull()
    }

    @Test
    fun `It should not be possible to create a new recipe with a 'negative' duration`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        categoryService.save(Category(name = "abc"))
        val request = CreateRecipeRequest(
            "woop",
            "woop",
            mutableListOf("woop"),
            true,
            Duration.ofHours(-1),
            Duration.ofHours(-2),
            3
        ).apply {
            categories = mutableSetOf("abc")
            tags = mutableSetOf("def")
        }

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<Unit>("/recipe", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0])
    fun `It should not be possible to create a new recipe with a yield of zero or less`(yield: Int) {
        val author = userService.createUser("test@test.com", "woop", "secret")
        categoryService.save(Category(name = "abc"))
        val request = CreateRecipeRequest(
            "woop",
            "woop",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            `yield`
        ).apply {
            categories = mutableSetOf("abc")
            tags = mutableSetOf("def")
        }

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<Unit>("/recipe", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `It should be possible to replace an old recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)

        for (name in listOf("keep", "remove")) {
            recipe.categories.add(Category(name = name))
        }

        for (name in listOf("keep", "remove")) {
            recipe.tags.add(Tag(name = name, recipe = recipe))
        }

        for (name in listOf("keep", "remove")) {
            recipe.ingredients.add(Ingredient(name = name))
        }

        recipeService.save(recipe)

        val request = CreateRecipeRequest(
            "wawawa",
            "wawawa",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        ).apply {
            categories = mutableSetOf("keep")
            tags = mutableSetOf("keep", "add")
            ingredients = mutableSetOf("keep", "add")
        }

        restTemplate.withBasicAuth(author.userName, "secret")
            .put("/recipe/${recipe.id}", request)

        val result = recipeService.getById(recipe.id!!).get()

        expectThat(result.title).isEqualTo(request.title)
        expectThat(result.description).isEqualTo(request.description)
        expectThat(result.public).isEqualTo(request.public)
        expectThat(result.prepTime).isEqualTo(request.prepTime)
        expectThat(result.cookTime).isEqualTo(request.cookTime)
        expectThat(result.yield).isEqualTo(request.yield)
        expectThat(result.categories).map(Category::name).containsExactlyInAnyOrder("keep")
        expectThat(result.tags).map(Tag::name).containsExactlyInAnyOrder("keep", "add")
        expectThat(result.ingredients).map(Ingredient::name).containsExactlyInAnyOrder("keep", "add")

        // Categories and ingredients should not be removed once created
        expectThat(categoryService.getByName("remove")).isPresent()
        expectThat(ingredientService.getByName("remove")).isPresent()

        // Categories, ingredients and tags should not be recreated if already existing
        val categoryId = recipe.categories.find { it.name == "keep" }!!.id
        expectThat(categoryService.getByName("keep")).isPresent()
            .get { expectThat(id).isEqualTo(categoryId) }

        val ingredientId = recipe.ingredients.find { it.name == "keep" }!!.id
        expectThat(ingredientService.getByName("keep")).isPresent()
            .get { expectThat(id).isEqualTo(ingredientId) }

        val tagId = recipe.tags.find { it.name == "keep" }!!.id
        expectThat(tagService.getByNameAndRecipe("keep", recipe)).isPresent()
            .get { expectThat(id).isEqualTo(tagId) }
    }

    @Test
    fun `It should be possible to replace an old recipe without ratings disappearing`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        ratingService.rateAsUser(author, recipe, 5)

        val request = CreateRecipeRequest(
            "wawawa",
            "wawawa",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        )

        restTemplate.withBasicAuth(author.userName, "secret")
            .put("/recipe/${recipe.id}", request)

        val result = restTemplate.withBasicAuth(author.userName, "secret")
            .getForObject<RecipeDto>("/recipe/${recipe.id}")!!

        expectThat(result.rating?.count).isNotNull().isNotEqualTo(0)
    }

    @Test
    fun `It should be possible to replace an old recipe without images disappearing`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        val upload = imageUploadRepository.save(createUpload())
        recipeHasUploadRepository.save(createOriginalMapping(upload, recipe))

        val request = CreateRecipeRequest(
            "wawawa",
            "wawawa",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        )

        restTemplate.withBasicAuth(author.userName, "secret")
            .put("/recipe/${recipe.id}", request)

        val result = restTemplate.withBasicAuth(author.userName, "secret")
            .getForObject<RecipeDto>("/recipe/${recipe.id}")!!

        expectThat(result.images).isNotEmpty()
    }

    @Test
    fun `It should be possible to update an old recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)

        for (name in listOf("keep", "remove")) {
            recipe.categories.add(Category(name = name))
        }

        for (name in listOf("keep", "remove")) {
            recipe.tags.add(Tag(name = name, recipe = recipe))
        }

        for (name in listOf("keep", "remove")) {
            recipe.ingredients.add(Ingredient(name = name))
        }

        recipeService.save(recipe)

        val request = UpdateRecipeRequest(
            "wawawa",
            "wawawa",
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3,
            mutableListOf("woop"),
            mutableSetOf("keep", "add"),
            mutableSetOf("keep"),
            mutableSetOf("keep", "add")
        )

        restTemplate.withBasicAuth(author.userName, "secret")
            .patchForObject<Unit>("/recipe/${recipe.id}", request)

        val result = recipeService.getById(recipe.id!!).get()

        expectThat(result.title).isEqualTo(request.title)
        expectThat(result.description).isEqualTo(request.description)
        expectThat(result.public).isEqualTo(request.public)
        expectThat(result.prepTime).isEqualTo(request.prepTime)
        expectThat(result.cookTime).isEqualTo(request.cookTime)
        expectThat(result.yield).isEqualTo(request.yield)
        expectThat(result.categories).map(Category::name).containsExactlyInAnyOrder("keep")
        expectThat(result.tags).map(Tag::name).containsExactlyInAnyOrder("keep", "add")
        expectThat(result.ingredients).map(Ingredient::name).containsExactlyInAnyOrder("keep", "add")

        // Categories and ingredients should not be removed once created
        expectThat(categoryService.getByName("remove")).isPresent()
        expectThat(ingredientService.getByName("remove")).isPresent()

        // Categories, ingredients and tags should not be recreated if already existing
        val categoryId = recipe.categories.find { it.name == "keep" }!!.id
        expectThat(categoryService.getByName("keep")).isPresent()
            .get { expectThat(id).isEqualTo(categoryId) }

        val ingredientId = recipe.ingredients.find { it.name == "keep" }!!.id
        expectThat(ingredientService.getByName("keep")).isPresent()
            .get { expectThat(id).isEqualTo(ingredientId) }

        val tagId = recipe.tags.find { it.name == "keep" }!!.id
        expectThat(tagService.getByNameAndRecipe("keep", recipe)).isPresent()
            .get { expectThat(id).isEqualTo(tagId) }
    }

    @Test
    fun `It should be possible to fetch a recipe by id`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)
        recipeService.save(recipe)

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getForEntity<RecipeDto>("/recipe/${recipe.id}")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull()
        expectThat(response.body!!.id).isEqualTo(recipe.id)
    }

    @Test
    fun `It should return 404 if trying to fetch a non-existing recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getForEntity<Unit>("/recipe/42")

        expectThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `It should be possible to delete a recipe by id`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)
        recipeService.save(recipe)

        restTemplate.withBasicAuth(author.userName, "secret")
            .delete("/recipe/${recipe.id}")

        expectThat(recipeService.getById(recipe.id!!)).isAbsent()
    }

    @Test
    fun `It should return 201 if trying to delete a non-existing recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")

        expectCatching {
            restTemplate.withBasicAuth(author.userName, "secret")
                .delete("/recipe/42")
        }.isSuccess()
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
            type = RecipeHasImageUpload.Type.ORIGINAL,
            recipe = recipe,
            upload = upload
        )
    }
}
