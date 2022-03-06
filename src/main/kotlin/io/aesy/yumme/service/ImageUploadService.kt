package io.aesy.yumme.service

import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.image.Alignment
import io.aesy.yumme.image.BufferedImages.crop
import io.aesy.yumme.image.BufferedImages.removeTransparency
import io.aesy.yumme.image.BufferedImages.rescaleToCover
import io.aesy.yumme.logging.Logging.getLogger
import io.aesy.yumme.repository.ImageUploadRepository
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.storage.Storage
import io.aesy.yumme.util.MD5
import io.aesy.yumme.util.Slugs
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO
import javax.transaction.Transactional
import kotlin.math.min

@Service
class ImageUploadService(
    private val storage: Storage,
    private val imageUploadRepository: ImageUploadRepository,
    private val recipeHasImageUploadRepository: RecipeHasImageUploadRepository
) {
    companion object {
        private const val FILENAME_PREFIX = "recipe-image"

        private val logger = getLogger()
    }

    @Cacheable("default-upload-image")
    @Synchronized
    fun getDefaultImage(type: Type): BufferedImage {
        throw NotImplementedError()
    }

    @Transactional
    fun getUpload(recipe: Recipe, name: String, type: Type): Optional<ImageUpload> {
        return recipeHasImageUploadRepository.findByRecipeAndNameAndType(recipe, name, type)
            .map(RecipeHasImageUpload::upload)
    }

    @Transactional
    fun deleteUpload(recipe: Recipe, name: String): Boolean {
        val mappings = recipeHasImageUploadRepository.findByRecipeAndName(recipe, name)

        for (mapping in mappings) {
            storage.delete(mapping.upload.fileName)
            recipeHasImageUploadRepository.delete(mapping)
        }

        return mappings.isNotEmpty()
    }

    fun readImage(upload: ImageUpload): Optional<BufferedImage> {
        val image = try {
            upload.read()
        } catch (e: FileNotFoundException) {
            logger.warn("Image uploads' corresponding file on disk couldn't be found", e)
            return Optional.empty()
        } catch (e: IOException) {
            logger.error("Failed to read image ${upload.fileName}", e)
            return Optional.empty()
        }

        return Optional.of(image)
    }

    @Transactional
    @Throws(IOException::class)
    fun storeImage(recipe: Recipe, image: BufferedImage): RecipeHasImageUpload {
        val bytes = image.removeTransparency().toBytes()
        val imageName = generateImageName(recipe)
        val fileName = generateNewFileName(recipe)
        val hash = MD5.hash(bytes)
        val upload = ImageUpload(
            fileName = fileName,
            width = image.width,
            height = image.height,
            hash = hash
        )

        imageUploadRepository.save(upload)

        val mapping = RecipeHasImageUpload(
            name = imageName,
            type = Type.ORIGINAL,
            recipe = recipe,
            upload = upload
        )

        recipeHasImageUploadRepository.save(mapping)
        storage.write(fileName, bytes)

        return mapping
    }

    @Transactional
    @Scheduled(fixedRate = 10 * 1000)
    internal fun generateVariations() {
        val types = arrayOf(Type.LARGE, Type.MEDIUM, Type.THUMBNAIL)

        for (type in types) {
            val originals = recipeHasImageUploadRepository.findUnprocessedOriginalsByType(type.name)

            for (original in originals) {
                logger.info("Generating image variation of type '$type' for image upload '${original.upload.fileName}'")

                val recipe = original.recipe
                val image = original.upload.read()
                val cropped = image.rescaleToCover(type.dimensions)
                    .crop(type.dimensions, Alignment.CENTER, Alignment.CENTER)
                val bytes = cropped.toBytes()
                val fileName = generateFileNameVariation(original, type)
                val hash = MD5.hash(bytes)

                storage.write(fileName, bytes)

                val thumbnail = ImageUpload(
                    fileName = fileName,
                    width = cropped.width,
                    height = cropped.height,
                    hash = hash
                )

                imageUploadRepository.save(thumbnail)

                val mapping = RecipeHasImageUpload(
                    name = original.name,
                    type = type,
                    recipe = recipe,
                    upload = thumbnail
                )

                recipeHasImageUploadRepository.save(mapping)
            }
        }
    }

    @Transactional
    @Scheduled(fixedRate = 15 * 60 * 1000)
    internal fun deleteOrphanedImages() {
        logger.debug("Checking for orphaned image uploads")

        val paths = storage.listFiles()

        for (path in paths) {
            val upload = imageUploadRepository.findByFileName(path)

            if (upload.isEmpty) {
                logger.info("Deleting orphaned image upload '$path'")

                val deleted = storage.delete(path)

                if (!deleted) {
                    logger.warn("Failed to delete orphaned image upload '$path': Image not found")
                }
            }
        }
    }

    private fun generateImageName(recipe: Recipe): String {
        val name = recipe.title.substring(0, min(20, recipe.title.length))

        return Slugs.create(name, 30)
    }

    private fun generateNewFileName(recipe: Recipe): String {
        val name = recipe.title.substring(0, min(20, recipe.title.length))
        val slug = Slugs.create(name, 50)
        val suffix = Type.ORIGINAL.toFileNamePart()

        return "$FILENAME_PREFIX-$slug-$suffix.png"
    }

    private fun generateFileNameVariation(upload: RecipeHasImageUpload, variation: Type): String {
        val suffix = variation.toFileNamePart()

        return "$FILENAME_PREFIX-${upload.name}-$suffix.png"
    }

    private fun Type.toFileNamePart() = when (this) {
        Type.ORIGINAL -> "original"
        Type.LARGE -> "large"
        Type.MEDIUM -> "medium"
        Type.THUMBNAIL -> "thumbnail"
    }

    @Throws(IOException::class)
    private fun ImageUpload.read(): BufferedImage {
        return storage.read(fileName)
            .use(ImageIO::read)
    }

    @Throws(IOException::class)
    private fun BufferedImage.toBytes(): ByteArray {
        ByteArrayOutputStream().use {
            ImageIO.write(this, "png", it)

            return it.toByteArray()
        }
    }
}
