package io.aesy.yumme.repository

import io.aesy.yumme.entity.Collection
import io.aesy.yumme.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface CollectionRepository: CrudRepository<Collection, Long> {
    fun findByOwner(owner: User, pageable: Pageable = Pageable.unpaged()): List<Collection>
}
