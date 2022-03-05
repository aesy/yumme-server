package io.aesy.test.extension

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RedisExtension::class)
annotation class RedisSettings(
    /**
     * Determines whether the MariaDB extension is enabled.
     */
    val enabled: Boolean = true,

    /**
     * Determines whether the database should be cleaned between tests.
     */
    val clean: Boolean = true
)
