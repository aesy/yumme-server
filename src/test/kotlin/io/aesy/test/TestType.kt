package io.aesy.test

import io.aesy.test.extension.MariaDBExtension
import io.aesy.test.extension.RedisExtension
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

object TestType {
    /**
     * Marks/tags a test class as a unit test and registers the Mockk extension
     */
    @Tag("UnitTest")
    @ExtendWith(MockKExtension::class)
    annotation class Unit

    /**
     * Marks/tags a test class as an integration test and starts a MariaDB container in docker for persistence
     */
    @Tag("IntegrationTest")
    @ExtendWith(MariaDBExtension::class)
    @ExtendWith(RedisExtension::class)
    annotation class Integration

    /**
     * Marks/tags a test class as an persistence + integration test, loads spring with the minimal
     * set of classes required to test the persistence layer. A MariaDB container is started in docker.
     */
    @Tag("PersistenceTest")
    @Transactional(propagation = Propagation.NEVER)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @Integration
    annotation class Persistence

    /**
     * Starts a full webserver for testing of rest endpoints
     */
    @Tag("RestApiTest")
    @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    @Integration
    annotation class RestApi
}
