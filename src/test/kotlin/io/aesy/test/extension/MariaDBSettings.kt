package io.aesy.test.extension

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MariaDBExtension::class)
annotation class MariaDBSettings(
    /**
     * Determines whether the MariaDB extension is enabled.
     */
    val enabled: Boolean = true,

    /**
     * Determines whether the database should be cleaned between tests.
     */
    val clean: Boolean = true,

    /**
     * Determines whether Flyway migrations should be run before the tests.
     *
     * If both the `migrate` and `clean` flags are set to true, then the migrations are executed before each test
     * case. Otherwise, if `clean` is set to false, then the migrations are only ran once on startup.
     */
    val migrate: Boolean = true
)
