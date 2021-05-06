package io.aesy.yumme.service

import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.Collection
import io.aesy.yumme.repository.CollectionRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class CollectionService(
    private val collectionRepository: CollectionRepository
) {
    @Transactional
    fun getByOwner(owner: User, limit: Int = 0, offset: Int = 0): List<Collection> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return collectionRepository.findByOwner(owner, page)
    }

    @Transactional
    fun getById(id: Long): Optional<Collection> {
        return collectionRepository.findById(id)
    }

    @Transactional
    fun save(collection: Collection): Collection {
        return collectionRepository.save(collection)
    }

    @Transactional
    fun delete(collection: Collection) {
        collectionRepository.delete(collection)
    }
}
