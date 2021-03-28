package io.aesy.yumme.util

import org.modelmapper.ModelMapper

object ModelMapper {
    inline fun <reified T> ModelMapper.map(source: Any): T = map(source, T::class.java)
}
