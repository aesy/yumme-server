package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.ImageUploadDto
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.ImageUploadService
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.AccessControl.canRead
import io.aesy.yumme.util.AccessControl.canWrite
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.commons.imaging.ImageReadException
import org.apache.commons.imaging.Imaging
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.http.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.awt.image.BufferedImage
import java.io.IOException
import java.time.*
import java.time.format.DateTimeFormatter
import javax.transaction.Transactional

@Tag(name = "Recipe")
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class ImageController(
    private val recipeService: RecipeService,
    private val imageUploadService: ImageUploadService
) {
    companion object {
        private val CACHE_DURATION = Duration.ofHours(12)
    }

    @RequiresAuthentication
    @GetMapping("/recipe/{id}/image/{name}.png")
    @Transactional
    fun viewImage(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @PathVariable("name") name: String,
        @RequestParam("size", defaultValue = "large") size: String
    ): ResponseEntity<BufferedImage> {
        val type = size.toType()
        val recipe = recipeService.getById(id)
            .filter { user.canRead(it) }
            .orElseThrow { ResourceNotFound() }
        val response = ResponseEntity.ok()
        val upload = imageUploadService.getUpload(recipe, name, type)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found") }
        val image = imageUploadService.readImage(upload)

        image.ifPresent {
            val cacheControl = CacheControl.maxAge(CACHE_DURATION).cachePublic()
            val expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plus(CACHE_DURATION)
            val modifiedAt = OffsetDateTime.ofInstant(upload.modifiedAt, ZoneOffset.UTC)

            response
                .header(HttpHeaders.CACHE_CONTROL, cacheControl.headerValue)
                .header(HttpHeaders.EXPIRES, DateTimeFormatter.RFC_1123_DATE_TIME.format(expiresAt))
                .header(HttpHeaders.LAST_MODIFIED, DateTimeFormatter.RFC_1123_DATE_TIME.format(modifiedAt))
                .header(HttpHeaders.ETAG, upload.hash)
        }

        return response
            .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
            .body(image.orElseGet { imageUploadService.getDefaultImage(type) })
    }

    @RequiresAuthentication
    @PostMapping("/recipe/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun uploadImage(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @RequestParam("file") file: MultipartFile
    ): ImageUploadDto {
        val recipe = recipeService.getById(id)
            .filter { user.canWrite(it) }
            .orElseThrow { ResourceNotFound() }

        val size = try {
            Imaging.getImageSize(file.inputStream, file.originalFilename)
        } catch (e: ImageReadException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse image")
        } catch (e: IOException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read image")
        }

        val minimumSize = Type.ORIGINAL.dimensions

        if (size.width < minimumSize.width || size.height < minimumSize.height) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Image must be larger than ${minimumSize.width}x${minimumSize.height}"
            )
        }

        val upload = try {
            imageUploadService.storeImage(recipe, file.inputStream)
        } catch (e: IOException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process image")
        }

        return ImageUploadDto("${upload.name}.png")
    }

    @RequiresAuthentication
    @DeleteMapping("/recipe/{id}/image/{name}.png")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteImage(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @PathVariable("name") name: String,
    ) {
        val recipe = recipeService.getById(id)
            .filter { user.canWrite(it) }
            .orElseThrow { ResourceNotFound() }

        val deleted = imageUploadService.deleteUpload(recipe, name)

        if (!deleted) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found")
        }
    }

    private fun String.toType() = when (this) {
        "thumbnail" -> Type.THUMBNAIL
        "medium" -> Type.MEDIUM
        "large" -> Type.LARGE
        else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown size type '$this'")
    }
}
