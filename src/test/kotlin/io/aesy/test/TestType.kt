package io.aesy.test

import io.aesy.test.extension.MariaDBSettings
import io.aesy.test.extension.RedisSettings
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.ActiveProfiles
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
     * Marks/tags a test class as an integration test using Spring
     */
    @Tag("IntegrationTest")
    @SpringBootTest(webEnvironment = WebEnvironment.NONE)
    @ActiveProfiles("test")
    annotation class Integration

    /**
     * Marks/tags a test class as a persistence test which loads Spring with the minimal
     * set of classes required to test the persistence layer, including a MariaDB database.
     */
    @Tag("PersistenceTest")
    @MariaDBSettings(clean = false) // No need to clean between JPA tests because all transactions are rolled back
    @DataJpaTest
    @Transactional(propagation = Propagation.NEVER)
    @AutoConfigureTestDatabase(replace = Replace.NONE)
    @ActiveProfiles("test")
    annotation class Persistence

    /**
     * Starts a full webserver for testing of rest endpoints
     */
    @Tag("RestApiTest")
    @MariaDBSettings
    @RedisSettings
    @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("test")
    annotation class RestApi
}
