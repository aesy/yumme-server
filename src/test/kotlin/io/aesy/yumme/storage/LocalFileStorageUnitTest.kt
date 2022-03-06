package io.aesy.yumme.storage

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files

@TestType.Unit
class LocalFileStorageUnitTest {
    @Test
    fun `It should be able to read and write bytes from disk`() {
        val filename = "test.txt"
        val contents = "woop"
        val directory = Files.createTempDirectory("YummeIntegrationTest")
        val storage = LocalFileStorage(directory)

        storage.write(filename, contents.encodeToByteArray())

        expectThat(Files.exists(directory.resolve(filename))).isTrue()

        val readContents = storage.read(filename)
            .use(InputStream::readAllBytes)
            .decodeToString()

        expectThat(readContents).isEqualTo(contents)
    }

    @Test
    fun `It should be able to delete a file from disk`() {
        val filename = "test.txt"
        val directory = Files.createTempDirectory("YummeIntegrationTest")
        val storage = LocalFileStorage(directory)

        storage.write(filename, "woop".encodeToByteArray())

        expectThat(Files.exists(directory.resolve(filename))).isTrue()

        val deleted = storage.delete(filename)

        expectThat(deleted).isTrue()
        expectThat(Files.exists(directory.resolve(filename))).isFalse()
        expectThrows<IOException> { storage.read(filename) }
    }
}
