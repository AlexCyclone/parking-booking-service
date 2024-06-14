package icu.cyclone.parking.booking

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class BookingServiceApplicationTests {
    @Test
    fun contextLoads() {
    }

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer<Nothing>("postgres:latest").apply {
                withDatabaseName("test")
                withUsername("test")
                withPassword("test")
            }

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }
}
