package io.aesy.food

import org.modelmapper.ModelMapper
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean



@SpringBootApplication
class FoodApplication {
    @Bean
    fun modelMapper(): ModelMapper {
        return ModelMapper()
    }
}

fun main(args: Array<String>) {
    runApplication<FoodApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
