package io.aesy.yumme.conversion

import org.modelmapper.ModelMapper

interface MapperConfigurer {
    fun configure(mapper: ModelMapper)
}
