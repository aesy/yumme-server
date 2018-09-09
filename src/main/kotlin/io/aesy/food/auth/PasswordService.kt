package io.aesy.food.auth

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class PasswordService {
    companion object {
        private val logger = LoggerFactory.getLogger(PasswordService::class.java)
    }
}
