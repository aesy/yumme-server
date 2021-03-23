package io.aesy.yumme.converter

import io.aesy.test.TestType
import io.aesy.yumme.conversion.*
import io.aesy.yumme.entity.*
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Unit
@SpringBootTest(
    classes = [
        MapperConfiguration::class,
        CategoryMapperConfigurer::class,
    ]
)
class CategoryMapperUnitTest {
    @Autowired
    private lateinit var modelMapper: ModelMapper

    @Test
    fun `It should be possible to convert a set of categories to a set of strings`() {
        val categories = mutableSetOf<Category>()
        categories.add(Category(name = "first"))
        categories.add(Category(name = "second"))

        val strings = modelMapper.map<Set<String>>(categories, object: TypeToken<Set<String>>() {}.type)

        expectThat(strings).containsExactly("first", "second")
    }
}
