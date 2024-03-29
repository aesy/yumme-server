package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import io.aesy.yumme.logging.Logging.getLogger
import io.aesy.yumme.repository.RecipeRepository
import org.springframework.beans.factory.ObjectProvider
import org.springframework.cache.annotation.*
import org.springframework.data.domain.*
import org.springframework.data.domain.Sort.Direction
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val queryBuilderProvider: ObjectProvider<RecipeQueryBuilder>
) {
    companion object {
        private val logger = getLogger()
    }

    fun query(): RecipeQueryBuilder {
        return queryBuilderProvider.getObject()
    }

    @Cacheable("recent-recipes")
    @Transactional
    fun getRecent(limit: Int = 0, offset: Int = 0): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name, Recipe::id.name)
        val page = if (limit > 0) {
            PageRequest.of(offset, limit, sort)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAllPublic(page)
    }

    @Transactional
    fun getRecentByUser(user: User, limit: Int = 0, offset: Int = 0): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name, Recipe::id.name)
        val page = if (limit > 0) {
            PageRequest.of(offset, limit, sort)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAllPublicByAuthor(user, page)
    }

    @Cacheable("popular-recipes")
    @Transactional
    fun getPopular(limit: Int = 0, offset: Int = 0): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name, Recipe::id.name) // TODO use popularity
        val page = if (limit > 0) {
            PageRequest.of(offset, limit, sort)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAllPublic(page)
    }

    @Transactional
    fun getPopularByUser(user: User, limit: Int = 0, offset: Int = 0): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name, Recipe::id.name) // TODO use popularity
        val page = if (limit > 0) {
            PageRequest.of(offset, limit, sort)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAllPublicByAuthor(user, page)
    }

    @Transactional
    fun getById(id: Long): Optional<Recipe> {
        return recipeRepository.findById(id)
    }

    @Transactional
    fun getByAuthor(author: User, limit: Int = 0, offset: Int = 0): List<Recipe> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return recipeRepository.findAllByAuthor(author, page)
    }

    @Transactional
    fun save(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    @Transactional
    fun delete(recipe: Recipe) {
        recipeRepository.delete(recipe)
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    @Caching(
        evict = [
            CacheEvict("popular-recipes", allEntries = true),
            CacheEvict("recent-recipes", allEntries = true)
        ]
    )
    internal fun evictCache() {
        logger.debug("Evicting recipe cache")
    }
}
