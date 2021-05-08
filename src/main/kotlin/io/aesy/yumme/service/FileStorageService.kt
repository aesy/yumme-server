package io.aesy.yumme.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.*
import java.nio.file.*
import java.util.stream.Stream

@Service
class FileStorageService(
    @Value("\${yumme.storage.directory}")
    private val directory: Path
) {
    init {
        if (!Files.isDirectory(directory)) {
            Files.createDirectories(directory)
        }
    }

    @Throws(IOException::class)
    fun read(filename: String): InputStream {
        val file = directory.resolve(filename).toFile()

        return BufferedInputStream(FileInputStream(file))
    }

    @Throws(IOException::class)
    fun getAllFilePaths(): Stream<Path> {
        return Files.walk(directory, 1)
            .filter(Files::isRegularFile)
    }

    @Throws(IOException::class)
    fun write(filename: String, bytes: ByteArray) {
        val filePath = directory.resolve(filename)
        val stream = BufferedInputStream(ByteArrayInputStream(bytes))

        stream.use {
            Files.copy(it, filePath, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    @Throws(IOException::class)
    fun delete(filename: String): Boolean {
        val path = directory.resolve(filename)

        return Files.deleteIfExists(path)
    }
}
