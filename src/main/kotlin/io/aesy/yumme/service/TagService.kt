package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.Tag
import io.aesy.yumme.repository.TagRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    @Transactional
    fun getAll(): List<Tag> {
        return tagRepository.findAll()
            .toList()
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe): List<Tag> {
        return recipe.tags.toList()
    }

    @Transactional
    fun getById(id: Long): Optional<Tag> {
        return tagRepository.findById(id)
    }

    @Transactional
    fun getByName(name: String): Optional<Tag> {
        return tagRepository.findByName(name)
    }

    @Transactional
    fun save(tag: Tag): Tag {
        return tagRepository.save(tag)
    }

    @Transactional
    fun delete(tag: Tag): Boolean {
        tagRepository.delete(tag)

        return true
    }

    @Transactional
    fun delete(id: Long): Boolean {
        tagRepository.deleteById(id)

        return true
    }
}
