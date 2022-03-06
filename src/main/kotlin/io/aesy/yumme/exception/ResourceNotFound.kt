package io.aesy.yumme.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
class ResourceNotFound private constructor(message: String): YummeException(message) {
    companion object {
        fun recipe(id: Long): ResourceNotFound = ResourceNotFound("No recipe with id $id")
        fun collection(id: Long): ResourceNotFound = ResourceNotFound("No collection with id $id")
        fun imageUpload(name: String): ResourceNotFound = ResourceNotFound("No image upload with name $name")
        fun user(id: Long): ResourceNotFound = ResourceNotFound("No user with id $id")
    }
}
