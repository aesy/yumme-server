package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.Tag
import io.aesy.yumme.repository.TagRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    @Transactional
    fun getAll(limit: Int = 0, offset: Int = 0): List<Tag> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return tagRepository.findAll(page)
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe, limit: Int = 0, offset: Int = 0): List<Tag> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return tagRepository.findAllByRecipe(recipe, page)
    }

    @Transactional
    fun save(tag: Tag): Tag {
        return tagRepository.save(tag)
    }
}
