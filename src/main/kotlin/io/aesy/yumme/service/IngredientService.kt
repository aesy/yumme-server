package io.aesy.yumme.service

import io.aesy.yumme.entity.Ingredient
import io.aesy.yumme.repository.IngredientRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class IngredientService(
    private val ingredientRepository: IngredientRepository
) {
    @Transactional
    fun getByName(name: String): Optional<Ingredient> {
        return ingredientRepository.findByName(name)
    }

    @Transactional
    fun save(ingredient: Ingredient): Ingredient {
        return ingredientRepository.save(ingredient)
    }

    @Transactional
    fun delete(ingredient: Ingredient) {
        ingredientRepository.delete(ingredient)
    }
}
