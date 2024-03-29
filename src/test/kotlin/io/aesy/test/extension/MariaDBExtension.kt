package io.aesy.test.extension

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MariaDBContainer
import java.util.*

class MariaDBExtension:
    BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback, Store.CloseableResource {

    companion object {
        private const val DATABASE_NAME = "yumme-test"
        private var container: MariaDBContainer<Nothing>? = null
        private var enabled: Boolean = false
        private var migrate: Boolean = false
        private var clean: Boolean = false
    }

    @Synchronized
    override fun beforeAll(context: ExtensionContext) {
        val annotation = context.requiredTestClass.getAnnotation(MariaDBSettings::class.java)
        enabled = annotation?.enabled ?: true
        migrate = annotation?.migrate ?: true
        clean = annotation?.clean ?: true

        if (enabled && container == null) {
            container = createContainer()

            // Override application.yml
            System.setProperty("spring.datasource.url", container!!.jdbcUrl)
            System.setProperty("spring.datasource.username", container!!.username)
            System.setProperty("spring.datasource.password", container!!.password)

            // Register a callback for when the root test context is shut down
            context.root.getStore(Namespace.GLOBAL).put(MariaDBExtension::class.qualifiedName, this)
        }

        if (enabled && migrate && !clean) {
            migrate(context)
        }
    }

    override fun beforeEach(context: ExtensionContext) {
        if (enabled && migrate && clean) {
            migrate(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        if (enabled && migrate && !clean) {
            clean(context)
        }
    }

    override fun afterEach(context: ExtensionContext) {
        if (enabled && migrate && clean) {
            clean(context)
        }
    }

    @Synchronized
    override fun close() {
        container?.stop()
        container = null
    }

    private fun getFlyway(context: ApplicationContext): Optional<Flyway> = try {
        Optional.of(context.getBean())
    } catch (e: NoSuchBeanDefinitionException) {
        Optional.empty()
    }

    private fun createFlyway(): Flyway {
        val configuration = FluentConfiguration()
        configuration.dataSource(container!!.jdbcUrl, container!!.username, container!!.password)

        return configuration.load()
    }

    private fun getSpringContext(context: ExtensionContext): Optional<ApplicationContext> = try {
        Optional.of(SpringExtension.getApplicationContext(context))
    } catch (e: IllegalArgumentException) {
        Optional.empty()
    }

    private fun createContainer(): MariaDBContainer<Nothing> = MariaDBContainer<Nothing>("mariadb:10.6.7")
        .apply {
            withDatabaseName(DATABASE_NAME)
            withUsername("test")
            withPassword("test")
            withReuse(true)
            start()
        }

    private fun clean(context: ExtensionContext) {
        getSpringContext(context)
            .flatMap(::getFlyway)
            .orElseGet(::createFlyway)
            .apply(Flyway::clean)
    }

    private fun migrate(context: ExtensionContext) {
        getSpringContext(context)
            .flatMap(::getFlyway)
            .orElseGet(::createFlyway)
            .apply(Flyway::migrate)
    }
}
