package io.aesy.yumme.service

import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.repository.ImageUploadRepository
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.util.Images
import io.aesy.yumme.util.Logging.getLogger
import io.aesy.yumme.util.MD5
import org.apache.commons.imaging.*
import org.apache.commons.imaging.formats.png.PngConstants
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import java.util.zip.Deflater
import javax.transaction.Transactional
import kotlin.math.min

@Service
class ImageUploadService(
    private val fileStorageService: FileStorageService,
    private val slugService: SlugService,
    private val imageUploadRepository: ImageUploadRepository,
    private val recipeHasImageUploadRepository: RecipeHasImageUploadRepository
) {
    companion object {
        private const val FILENAME_PREFIX = "recipe-image"

        private val logger = getLogger()
    }

    @Cacheable("default-upload-image")
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
            fileStorageService.delete(mapping.upload.fileName)
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
        } catch (e: ImageReadException) {
            logger.error("Failed to process image ${upload.fileName}", e)

            return Optional.empty()
        }

        return Optional.of(image)
    }

    @Transactional
    @Throws(IOException::class)
    fun storeImage(recipe: Recipe, stream: InputStream): RecipeHasImageUpload {
        val image = try {
            Imaging.getBufferedImage(stream).also(Images::removeTransparency)
        } catch (e: ImageReadException) {
            logger.error("Failed to store image: Couldn't read image stream", e)
            throw IOException(e)
        }

        val bytes = try {
            image.toBytes()
        } catch (e: ImageWriteException) {
            logger.error("Failed to store image: Couldn't write image bytes", e)
            throw IOException(e)
        }

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
        fileStorageService.write(fileName, bytes)

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
                val scaled = Images.rescaleToFit(image, type.dimensions)
                val bytes = scaled.toBytes()
                val fileName = generateFileNameVariation(original, type)
                val hash = MD5.hash(bytes)

                fileStorageService.write(fileName, bytes)

                val thumbnail = ImageUpload(
                    fileName = fileName,
                    width = scaled.width,
                    height = scaled.height,
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
        logger.debug("Checking for ophaned image uploads")

        val paths = fileStorageService.getAllFilePaths()

        for (path in paths) {
            val fileName = path.fileName.toString()
            val upload = imageUploadRepository.findByFileName(fileName)

            if (upload.isEmpty) {
                logger.info("Deleting orphaned image upload '$fileName'")

                val deleted = fileStorageService.delete(fileName)

                if (!deleted) {
                    logger.warn("Failed to delete orphaned image upload '$fileName': Image not found")
                }
            }
        }
    }

    private fun generateImageName(recipe: Recipe): String {
        val name = recipe.title.substring(0, min(20, recipe.title.length))

        return slugService.generateSlug(name, 30)
    }

    private fun generateNewFileName(recipe: Recipe): String {
        val name = recipe.title.substring(0, min(20, recipe.title.length))
        val slug = slugService.generateSlug(name, 50)
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

    @Throws(IOException::class, ImageReadException::class)
    private fun ImageUpload.read(): BufferedImage {
        val file = fileStorageService.read(fileName)
        val bytes = file.readAllBytes()

        return Imaging.getBufferedImage(bytes)
    }

    @Throws(IOException::class, ImageWriteException::class)
    private fun BufferedImage.toBytes(): ByteArray {
        val params = mutableMapOf<String, Any>()
        params[PngConstants.PARAM_KEY_PNG_COMPRESSION_LEVEL] = Deflater.DEFLATED

        return Imaging.writeImageToBytes(this, ImageFormats.PNG, params)
    }
}