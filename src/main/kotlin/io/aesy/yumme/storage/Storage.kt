package io.aesy.yumme.storage

import java.io.IOException
import java.io.InputStream

interface Storage {
    @Throws(IOException::class)
    fun read(filename: String): InputStream

    @Throws(IOException::class)
    fun listFiles(): List<String>

    @Throws(IOException::class)
    fun write(filename: String, bytes: ByteArray)

    @Throws(IOException::class)
    fun delete(filename: String): Boolean
}
