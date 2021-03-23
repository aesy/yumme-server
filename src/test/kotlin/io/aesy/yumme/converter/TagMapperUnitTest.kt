package io.aesy.yumme.converter

import io.aesy.test.TestType
import io.aesy.yumme.conversion.MapperConfiguration
import io.aesy.yumme.conversion.TagMapperConfigurer
import io.aesy.yumme.entity.*
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import strikt.api.expectThat
import strikt.assertions.containsExactly

@TestType.Unit
@SpringBootTest(
    classes = [
        MapperConfiguration::class,
        TagMapperConfigurer::class
    ]
)
class TagMapperUnitTest {
    @Autowired
    private lateinit var modelMapper: ModelMapper

    @Test
    fun `It should be possible to convert a set of tags to a set of strings`() {
        val user = User(email = "test@test.com", password = "secret")
        val recipe = Recipe(title = "title", description = "description", author = user)
        val tags = mutableSetOf<Tag>()
        tags.add(Tag(name = "first", recipe = recipe))
        tags.add(Tag(name = "second", recipe = recipe))

        val strings = modelMapper.map<Set<String>>(tags, object: TypeToken<Set<String>>() {}.type)

        expectThat(strings).containsExactly("first", "second")
    }
}
