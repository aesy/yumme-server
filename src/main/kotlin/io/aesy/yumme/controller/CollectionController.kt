package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.CollectionDto
import io.aesy.yumme.entity.Collection
import io.aesy.yumme.repository.CollectionRepository
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