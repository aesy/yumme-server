package io.aesy.food.controller

import io.aesy.food.conversion.ResponseBodyType
import io.aesy.food.dto.TagDto
import io.aesy.food.entity.Tag
import io.aesy.food.exception.ResourceNotFound
import io.aesy.food.service.RecipeService
import io.aesy.food.service.TagService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class TagController(
    private val recipeService: RecipeService,
    private val tagService: TagService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TagController::class.java)
    }

    @GetMapping("/tag")
    @Transactional
    @ResponseBodyType(type = TagDto::class)
    fun list(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Tag> {
        return tagService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    @GetMapping("/recipe/{id}/tag")
    @Transactional
    @ResponseBodyType(type = TagDto::class)
    fun listByRecipe(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Tag> {
        val recipe = recipeService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return tagService.getAllByRecipe(recipe)
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }
}
