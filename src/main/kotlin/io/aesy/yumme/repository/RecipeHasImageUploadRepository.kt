package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.RecipeHasImageUpload
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RecipeHasImageUploadRepository: CrudRepository<RecipeHasImageUpload, Long> {
    fun findByRecipeAndType(recipe: Recipe, type: Type): List<RecipeHasImageUpload>
    fun findByRecipeAndName(recipe: Recipe, name: String): List<RecipeHasImageUpload>
    fun findByRecipeAndNameAndType(recipe: Recipe, name: String, type: Type): Optional<RecipeHasImageUpload>

    @Query(value = "CALL proc_unprocessed_originals_by_type(:type)", nativeQuery = true)
    fun findUnprocessedOriginalsByType(type: String): List<RecipeHasImageUpload>
}
