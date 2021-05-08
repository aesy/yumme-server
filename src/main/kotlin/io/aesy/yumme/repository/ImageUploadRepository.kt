package io.aesy.yumme.repository

import io.aesy.yumme.entity.ImageUpload
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ImageUploadRepository: CrudRepository<ImageUpload, Long> {
    fun findByFileName(fileName: String): Optional<ImageUpload>
}
