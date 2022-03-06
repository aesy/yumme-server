package io.aesy.yumme.storage

import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.*
import java.util.concurrent.ConcurrentHashMap

@Service
@ConditionalOnProperty("yumme.storage.type", havingValue = "memory")
class InMemoryStorage: Storage {
    private val files = ConcurrentHashMap<String, ByteArray>()

    @Throws(IOException::class)
    override fun read(filename: String): InputStream {
        val file = files[filename] ?: throw IOException("File doesn't exist: $filename")

        return ByteArrayInputStream(file)
    }

    override fun listFiles(): List<String> {
        return files.keys().toList()
    }

    override fun write(filename: String, bytes: ByteArray) {
        files[path + filename] = bytes
    }

    override fun delete(filename: String): Boolean {
        val removed = files.remove(path + filename)

        return removed != null
    }
}
