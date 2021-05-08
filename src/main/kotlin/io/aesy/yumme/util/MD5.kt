package io.aesy.yumme.util

import java.security.MessageDigest

object MD5 {
    fun hash(bytes: ByteArray): String {
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(bytes)

        return md5.digest()
            .joinToString("", transform = "%02x"::format)
    }
}
