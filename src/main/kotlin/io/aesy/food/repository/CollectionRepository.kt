package io.aesy.food.repository

import io.aesy.food.entity.Collection
import io.aesy.food.entity.User
import org.springframework.data.repository.CrudRepository

interface CollectionRepository: CrudRepository<Collection, Long> {
    fun findByOwner(owner: User): Set<Collection>
}
