package io.aesy.test.extension

import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import java.util.*

class RedisExtension: BeforeAllCallback, AfterEachCallback, Store.CloseableResource {

    companion object {
        private var container: GenericContainer<Nothing>? = null
    }

    @Synchronized
    override fun beforeAll(context: ExtensionContext) {
        if (container == null) {
            container = create()

            // Override application.yml
            System.setProperty("spring.cache.type", "redis")
            System.setProperty("spring.redis.host", container!!.host)
            System.setProperty("spring.redis.port", container!!.firstMappedPort.toString())

            // Register a callback for when the root test context is shut down
            context.root.getStore(Namespace.GLOBAL).put(RedisExtension::class.qualifiedName, this)
        }
    }

    override fun afterEach(context: ExtensionContext) {
        getSpringContext(context)
            .flatMap(::getRedis)
            .orElseGet(::createRedis)
            .apply(::clean)
    }

    override fun close() {
        container?.stop()
        container = null
    }

    private fun getRedis(context: ApplicationContext): Optional<RedisTemplate<String, *>> = try {
        Optional.of(context.getBean<RedisTemplate<String, *>>("redisTemplate"))
    } catch (e: NoSuchBeanDefinitionException) {
        Optional.empty()
    }

    private fun createRedis(): RedisTemplate<String, *> {
        val config = RedisStandaloneConfiguration()
        config.hostName = container!!.host
        config.port = container!!.firstMappedPort
        val template = RedisTemplate<String, Any>()
        val connectionFactory = JedisConnectionFactory(config)
        connectionFactory.afterPropertiesSet()
        template.setConnectionFactory(connectionFactory)
        template.afterPropertiesSet()

        return template
    }

    private fun getSpringContext(context: ExtensionContext): Optional<ApplicationContext> = try {
        Optional.of(SpringExtension.getApplicationContext(context))
    } catch (e: IllegalArgumentException) {
        Optional.empty()
    }

    private fun create(): GenericContainer<Nothing> = GenericContainer<Nothing>("redis:6.2.6")
        .apply {
            withExposedPorts(6379)
            withReuse(true)
            start()
        }

    private fun clean(redis: RedisTemplate<String, *>) {
        redis.keys("*").forEach(redis::delete)
    }
}
