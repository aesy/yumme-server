package io.aesy.yumme

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YummeApplication

fun main(args: Array<String>) {
    runApplication<YummeApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
