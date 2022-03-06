package io.aesy.yumme.repository.matcher

import io.aesy.yumme.entity.*
import io.aesy.yumme.util.Criteria.get
import io.aesy.yumme.util.Criteria.join
import org.springframework.data.jpa.domain.Specification
import java.time.Duration
import java.time.Instant

object RecipeMatcher {
    fun hasTitleLike(title: String): Specification<Recipe> {
        return Specification { root, _, builder ->
            val attribute = root.get(Recipe::title)

            builder.like(attribute, "%$title%")
        }
    }

    fun hasDescriptionLike(description: String): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::description)

            builder.like(attribute, "%$description%")
        }
    }

    fun hasDirectionsLike(directions: String): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::directions)

            builder.like(attribute, "%$directions%")
        }
    }

    fun hasMinPrepTime(duration: Duration): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::prepTime)

            builder.greaterThanOrEqualTo(attribute, duration)
        }
    }

    fun hasMaxPrepTime(duration: Duration): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::prepTime)

            builder.lessThanOrEqualTo(attribute, duration)
        }
    }

    fun hasMinCookTime(duration: Duration): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::cookTime)

            builder.greaterThanOrEqualTo(attribute, duration)
        }
    }

    fun hasMaxCookTime(duration: Duration): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::cookTime)

            builder.lessThanOrEqualTo(attribute, duration)
        }
    }

    fun byAuthor(userId: Long): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val users = root.join(Recipe::author)
            val attribute = users.get(User::id)

            builder.equal(attribute, userId)
        }
    }

    fun isPublic(public: Boolean): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::public)

            builder.equal(attribute, public)
        }
    }

    fun isCreatedBefore(time: Instant): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::createdAt)

            builder.lessThanOrEqualTo(attribute, time)
        }
    }

    fun isCreatedAfter(time: Instant): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val attribute = root.get(Recipe::createdAt)

            builder.greaterThanOrEqualTo(attribute, time)
        }
    }

    fun hasIngredient(ingredientId: Long): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val ingredients = root.join(Recipe::ingredients)
            val attribute = ingredients.get(Ingredient::id)

            builder.equal(attribute, ingredientId)
        }
    }

    fun hasTag(tagId: Long): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val tags = root.join(Recipe::tags)
            val attribute = tags.get(Tag::id)

            builder.equal(attribute, tagId)
        }
    }

    fun hasCategory(categoryId: Long): Specification<Recipe> {
        return Specification<Recipe> { root, _, builder ->
            val categories = root.join(Recipe::categories)
            val attribute = categories.get(Category::id)

            builder.equal(attribute, categoryId)
        }
    }
}
