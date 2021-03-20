package io.aesy.test

import org.junit.jupiter.api.extension.*
import org.testcontainers.containers.MariaDBContainer

class MariaDBExtension: BeforeAllCallback, AfterAllCallback {

    private lateinit var container: MariaDBContainer<Nothing>

    override fun beforeAll(context: ExtensionContext) {
        container = MariaDBContainer<Nothing>("mariadb:10").apply {
            withDatabaseName("yumme-test")
            withUsername("yumme-test")
            withPassword("yumme-test")
        }

        container.start()

        System.setProperty("spring.datasource.url", container.jdbcUrl)
        System.setProperty("spring.datasource.username", container.username)
        System.setProperty("spring.datasource.password", container.password)
    }

    override fun afterAll(context: ExtensionContext) {
        container.stop()
    }
}
