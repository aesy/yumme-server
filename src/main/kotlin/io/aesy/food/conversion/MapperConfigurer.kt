package io.aesy.food.conversion

import org.modelmapper.ModelMapper

abstract class MapperConfigurer {
    abstract fun configure(mapper: ModelMapper)
}
