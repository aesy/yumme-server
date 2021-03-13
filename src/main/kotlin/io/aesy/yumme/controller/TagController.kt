package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.TagDto
import io.aesy.yumme.entity.Tag
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.TagService
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
