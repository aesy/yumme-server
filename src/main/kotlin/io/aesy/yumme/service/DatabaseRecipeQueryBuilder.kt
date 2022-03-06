package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.RecipeRepository
import io.aesy.yumme.repository.matcher.RecipeMatcher
import io.aesy.yumme.util.Specifications
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.*
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import javax.transaction.Transactional

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class DatabaseRecipeQueryBuilder(
    private val recipeRepository: RecipeRepository
): RecipeQueryBuilder {
    private var spec: Specification<Recipe> = Specifications.default()

    override fun freetext(text: String): DatabaseRecipeQueryBuilder {
        spec = spec.and(
            RecipeMatcher.hasTitleLike(text)
                .or(RecipeMatcher.hasDescriptionLike(text))
                .or(RecipeMatcher.hasDirectionsLike(text))
        )
        return this
    }

    override fun withMinPrepTime(duration: Duration): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.hasMinPrepTime(duration))
        return this
    }

    override fun withMaxPrepTime(duration: Duration): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.hasMaxPrepTime(duration))
        return this
    }

    override fun withMinCookTime(duration: Duration): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.hasMinCookTime(duration))
        return this
    }

    override fun withMaxCookTime(duration: Duration): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.hasMaxCookTime(duration))
        return this
    }

    override fun byAuthor(userId: Long): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.byAuthor(userId))
        return this
    }

    override fun public(public: Boolean): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.isPublic(public))
        return this
    }

    override fun isCreatedBefore(time: Instant): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.isCreatedBefore(time))
        return this
    }

    override fun isCreatedAfter(time: Instant): DatabaseRecipeQueryBuilder {
        spec = spec.and(RecipeMatcher.isCreatedAfter(time))
        return this
    }

    override fun includingIngredients(ingredientIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (ingredient in ingredientIds) {
            spec = spec.and(RecipeMatcher.hasIngredient(ingredient))
        }

        return this
    }

    override fun notIncludingIngredients(ingredientIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (ingredient in ingredientIds) {
            spec = spec.and(Specification.not(RecipeMatcher.hasIngredient(ingredient)))
        }

        return this
    }

    override fun withTags(tagIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (tag in tagIds) {
            spec = spec.and(RecipeMatcher.hasTag(tag))
        }

        return this
    }

    override fun withoutTags(tagIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (tag in tagIds) {
            spec = spec.and(Specification.not(RecipeMatcher.hasTag(tag)))
        }

        return this
    }

    override fun inCategories(categoryIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (category in categoryIds) {
            spec = spec.and(RecipeMatcher.hasCategory(category))
        }

        return this
    }

    override fun notInCategories(categoryIds: List<Long>): DatabaseRecipeQueryBuilder {
        for (category in categoryIds) {
            spec = spec.and(Specification.not(RecipeMatcher.hasCategory(category)))
        }

        return this
    }

    @Transactional
    override fun search(limit: Int, offset: Int): List<Recipe> {
        val sort = Sort.unsorted()
        val page = if (limit > 0) {
            PageRequest.of(offset, limit, sort)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAll(spec, page).toList()
    }
}
