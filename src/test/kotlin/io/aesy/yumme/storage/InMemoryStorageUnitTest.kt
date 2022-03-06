package io.aesy.yumme.storage

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.io.IOException
import java.io.InputStream

@TestType.Unit
class InMemoryStorageUnitTest {
    private val storage = InMemoryStorage()

    @Test
    fun `It should be able to read and write bytes`() {
        val filename = "test.txt"
        val contents = "woop"

        storage.write(filename, contents.encodeToByteArray())

        val readContents = storage.read(filename)
            .use(InputStream::readAllBytes)
            .decodeToString()

        expectThat(readContents).isEqualTo(contents)
    }

    @Test
    fun `It should be able to delete a file`() {
        val filename = "test.txt"

        storage.write(filename, "woop".encodeToByteArray())
        val deleted = storage.delete(filename)

        expectThat(deleted).isTrue()
        expectThrows<IOException> { storage.read(filename) }
    }
}
