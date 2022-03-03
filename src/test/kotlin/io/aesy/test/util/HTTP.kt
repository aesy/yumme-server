package io.aesy.test.util

import io.aesy.yumme.util.Types
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

object HTTP {
    inline fun <reified T> TestRestTemplate.getList(path: String): ResponseEntity<List<T>> =
        exchange(path, HttpMethod.GET, null, Types.listOf())
}
