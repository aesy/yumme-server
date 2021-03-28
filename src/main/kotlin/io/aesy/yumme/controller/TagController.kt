package io.aesy.yumme.controller

import io.aesy.yumme.dto.TagDto
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.TagService
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Tag")
@RestController
class TagController(
    private val recipeService: RecipeService,
    private val tagService: TagService,
    private val mapper: ModelMapper
) {
    @GetMapping("/tag")
    @Transactional
    fun listTags(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<TagDto> {
        return tagService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<TagDto>(it) }
            .toList()
    }

    @GetMapping("/recipe/{id}/tag")
    @Transactional
    @Tag(name = "Recipe")
    fun listTagsByRecipe(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<TagDto> {
        val recipe = recipeService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return tagService.getAllByRecipe(recipe)
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<TagDto>(it) }
            .toList()
    }
}
