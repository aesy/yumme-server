package io.aesy.yumme.controller

import io.aesy.yumme.dto.CollectionDto
import io.aesy.yumme.repository.CollectionRepository
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Collection")
@RestController
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val mapper: ModelMapper
) {
    @GetMapping("collection")
    @Transactional
    fun listCollections(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<CollectionDto> {
        return collectionRepository.findAll()
            .map { mapper.map<CollectionDto>(it) }
            .toList()
    }
}
