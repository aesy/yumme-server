package io.aesy.food.conversion

import org.modelmapper.ModelMapper

interface MapperConfigurer {
    fun configure(mapper: ModelMapper)
}
