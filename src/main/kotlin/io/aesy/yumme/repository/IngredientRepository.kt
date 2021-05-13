package io.aesy.yumme.repository

import io.aesy.yumme.entity.Ingredient
import org.springframework.data.repository.CrudRepository
import java.util.*

interface IngredientRepository: CrudRepository<Ingredient, Long> {
    fun findByName(name: String): Optional<Ingredient>
}
