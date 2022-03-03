package io.aesy.test.extension

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MariaDBContainer
import java.util.*

class MariaDBExtension: BeforeAllCallback, BeforeEachCallback, AfterEachCallback, Store.CloseableResource {

    companion object {
        private const val DATABASE_NAME = "yumme-test"
        private var container: MariaDBContainer<Nothing>? = null
    }

    @Synchronized
    override fun beforeAll(context: ExtensionContext) {
        if (container == null) {
            container = create()

            // Override application.yml
            System.setProperty("spring.datasource.url", container!!.jdbcUrl)
            System.setProperty("spring.datasource.username", container!!.username)
            System.setProperty("spring.datasource.password", container!!.password)

            // Register a callback for when the root test context is shut down
            context.root.getStore(Namespace.GLOBAL).put(MariaDBExtension::class.qualifiedName, this)
        }
    }

    override fun beforeEach(context: ExtensionContext) {
        getSpringContext(context).ifPresent(::migrate)
    }

    override fun afterEach(context: ExtensionContext) {
        getSpringContext(context).ifPresent(::clean)
    }

    override fun close() {
        container?.stop()
        container = null
    }

    private fun getSpringContext(context: ExtensionContext): Optional<ApplicationContext> = try {
        Optional.of(SpringExtension.getApplicationContext(context))
    } catch (e: IllegalArgumentException) {
        Optional.empty()
    }

    private fun create(): MariaDBContainer<Nothing> = MariaDBContainer<Nothing>("mariadb:10.6.7")
        .apply {
            withDatabaseName(DATABASE_NAME)
            withUsername("test")
            withPassword("test")
            withReuse(true)
            start()
        }

    private fun clean(context: ApplicationContext) {
        val flyway = context.getBean<Flyway>()
        flyway.clean()
    }

    private fun migrate(context: ApplicationContext) {
        val flyway = context.getBean<Flyway>()
        flyway.migrate()
    }
}
