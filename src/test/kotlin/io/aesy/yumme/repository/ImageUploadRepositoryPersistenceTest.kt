package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.ImageUpload
import io.aesy.yumme.util.Strings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.java.isPresent

@TestType.Persistence
class ImageUploadRepositoryPersistenceTest {
    @Autowired
    private lateinit var imageUploadRepository: ImageUploadRepository

    @Test
    fun `It should be possible to persist an image upload`() {
        val upload = imageUploadRepository.save(createUpload())

        expectThat(upload.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch an image upload by file name`() {
        val upload = imageUploadRepository.save(createUpload())
        val result = imageUploadRepository.findByFileName(upload.fileName)

        expectThat(result).isPresent()
    }

    private fun createUpload(): ImageUpload {
        return ImageUpload(
            fileName = Strings.random(30),
            hash = Strings.random(32),
            width = 0,
            height = 0
        )
    }
}
