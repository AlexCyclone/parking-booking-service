package icu.cyclone.parking.booking

import java.nio.charset.Charset
import java.util.Date
import java.util.UUID

import javax.crypto.SecretKey

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import icu.cyclone.parking.booking.IntegrationTestParent.Companion.JWT_SECRET
import icu.cyclone.parking.booking.testcontainers.ForEachPostgresqlContainer
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = ["env.jwt.secret=$JWT_SECRET"])
abstract class IntegrationTestParent {

    @Autowired
    lateinit var mvc: MockMvc

    fun performGet(
        url: String,
        requestParams: Map<String, Any>,
        expectedStatus: HttpStatus,
    ): MvcResult = mvc.perform(
        MockMvcRequestBuilders.get(url)
            .withUserToken()
            .withRequestParams(requestParams)
            .accept(MediaType.APPLICATION_JSON_VALUE),
    ).andExpect(MockMvcResultMatchers.status().`is`(expectedStatus.value()))
        .andReturn()

    fun performPost(
        url: String,
        content: Any,
        expectedStatus: HttpStatus,
    ): MvcResult = mvc.perform(
        MockMvcRequestBuilders.post(url)
            .withUserToken()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(content.toJson()),
    ).andExpect(MockMvcResultMatchers.status().`is`(expectedStatus.value()))
        .andReturn()

    fun MockHttpServletRequestBuilder.withUserToken() = this.header("Authorization", "Bearer ${getSignedUserToken()}")

    fun MockHttpServletRequestBuilder.withRequestParams(requestParams: Map<String, Any>) = apply {
        requestParams.forEach { (key, value) ->
            this.queryParam(key, value.toString())
        }
    }

    fun Any.toJson(): String = objectMapper.writeValueAsString(this)

    fun <T> MvcResult.toDto(dtoClass: Class<T>): T = response.getContentAsString(CHARSET_UTF8).toDto(dtoClass)

    fun <T> MvcResult.toDtoList(dtoClass: Class<T>): List<T> = response.getContentAsString(CHARSET_UTF8).toDtoList(dtoClass)

    fun <T> String.toDto(dtoClass: Class<T>): T = objectMapper.readValue(this, dtoClass)

    fun <T> String.toDtoList(dtoClass: Class<T>): List<T> = objectMapper.readValue(
        this,
        objectMapper.typeFactory.constructCollectionType(
            List::class.java,
            dtoClass,
        ),
    )

    companion object {
        const val JWT_SECRET = "SldUX1NFQ1JFVF9MT05HRVJfVEhBTl8yNTZfQklUU19QTEFDRUhPTERFUg=="
        const val TEST_USERNAME = "user@test.cuclone.icu"
        val TEST_USER_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-000000000000")
        val CHARSET_UTF8 = Charset.forName("UTF-8")

        val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun getSignedUserToken(): String = getSignedToken("ROLE_USER")

        private fun getSignInKey(): SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET))

        private fun getSignedToken(role: String): String = Jwts
            .builder()
            .claims(
                mapOf(
                    "subjectId" to TEST_USER_ID,
                    "subjectRole" to role,
                ),
            )
            .subject(TEST_USERNAME)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 5 * 60 * 1000))
            .signWith(getSignInKey())
            .compact()

        @Container
        @JvmField
        val postgreSQLContainer = ForEachPostgresqlContainer.instance

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }
}
