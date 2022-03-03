package io.aesy.test.util

import java.io.InputStream

object Resources {
    @Throws(NoSuchResourceException::class)
    fun open(path: String): InputStream {
        return Resources::class.java.getResource(path)?.openStream()
            ?: throw NoSuchResourceException(path)
    }
}
