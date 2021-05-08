package io.aesy.yumme.service

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.nio.file.Files

@TestType.Unit
class FileStorageServiceUnitTest {
    @Test
    fun `It should be able to read and write bytes to and from disk`() {
        val writtenContents = "woop"
        val directory = Files.createTempDirectory("YummeIntegrationTest")
        val storage = FileStorageService(directory)
        storage.write("test.txt", writtenContents.encodeToByteArray())

        val readContents = storage.read("test.txt").readAllBytes().decodeToString()

        expectThat(readContents).isEqualTo(writtenContents)
    }
}
