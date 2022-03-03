package io.aesy.test.util

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import java.time.Duration

object Recipes {
    fun random(author: User): Recipe = Recipe(
        title = "woop",
        description = "woop",
        directions = "woop",
        prepTime = Duration.ofSeconds(1),
        cookTime = Duration.ofSeconds(1),
        yield = 1,
        author = author
    )
}
