package io.aesy.yumme.repository.matcher

import io.aesy.yumme.entity.Ingredient
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.util.Criteria.get
import io.aesy.yumme.util.Criteria.join
import org.springframework.data.jpa.domain.Specification

object IngredientMatcher {
    fun hasNameLike(name: String): Specification<Ingredient> {
        return Specification<Ingredient> { root, _, builder ->
            val attribute = root.get(Ingredient::name)

            builder.like(attribute, "%$name%")
        }
    }

    fun isInRecipe(recipe: Recipe): Specification<Ingredient> {
        return Specification<Ingredient> { root, _, builder ->
            val recipes = root.join(Ingredient::recipes)
            val attribute = recipes.get(Recipe::id)

            builder.equal(attribute, recipe.id)
        }
    }
}
