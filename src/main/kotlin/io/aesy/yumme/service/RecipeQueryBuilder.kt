package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import java.time.Duration
import java.time.Instant

interface RecipeQueryBuilder {
    fun freetext(text: String): RecipeQueryBuilder
    fun withMinPrepTime(duration: Duration): RecipeQueryBuilder
    fun withMaxPrepTime(duration: Duration): RecipeQueryBuilder
    fun withMinCookTime(duration: Duration): RecipeQueryBuilder
    fun withMaxCookTime(duration: Duration): RecipeQueryBuilder
    fun byAuthor(userId: Long): RecipeQueryBuilder
    fun public(public: Boolean): RecipeQueryBuilder
    fun isCreatedBefore(time: Instant): RecipeQueryBuilder
    fun isCreatedAfter(time: Instant): RecipeQueryBuilder
    fun includingIngredients(ingredientIds: List<Long>): RecipeQueryBuilder
    fun notIncludingIngredients(ingredientIds: List<Long>): RecipeQueryBuilder
    fun withTags(tagIds: List<Long>): RecipeQueryBuilder
    fun withoutTags(tagIds: List<Long>): RecipeQueryBuilder
    fun inCategories(categoryIds: List<Long>): RecipeQueryBuilder
    fun notInCategories(categoryIds: List<Long>): RecipeQueryBuilder
    fun search(limit: Int = 0, offset: Int = 0): List<Recipe>
}
