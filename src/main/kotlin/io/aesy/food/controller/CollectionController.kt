package io.aesy.food.controller

import io.aesy.food.conversion.ResponseBodyType
import io.aesy.food.dto.CollectionDto
import io.aesy.food.entity.Collection
import io.aesy.food.repository.CollectionRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class CollectionController(
    val collectionRepository: CollectionRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CollectionController::class.java)
    }

    @GetMapping("collection")
    @Transactional
    @ResponseBodyType(type = CollectionDto::class)
    fun list(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Collection> {
        return collectionRepository.findAll()
            .toList()
    }
}
