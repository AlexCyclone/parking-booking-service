package icu.cyclone.parking.booking.api

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import icu.cyclone.parking.booking.IntegrationTestParent
import icu.cyclone.parking.booking.client.vehicle.dto.UserVehicleDto
import icu.cyclone.parking.booking.dao.jpa.repository.ReservationRepository
import icu.cyclone.parking.booking.dto.ReservationDto
import icu.cyclone.parking.booking.dto.ReservationRequestDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@AutoConfigureWireMock(port = 0)
@ActiveProfiles("rest-mode", "rest-mode-mock")
class SpotControllerRestModeIntegration : IntegrationTestParent() {

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @AfterEach
    fun cleanDb() {
        reservationRepository.deleteAll()
        WireMock.reset()
    }

    @Test
    fun `add new vehicle during book empty spot`() {
        // given
        val requestedSpot = 1

        val findVehiclesStub = prepareFindVehiclesStub(emptyList())
        val addVehicleStub = prepareAddVehicleStub(TEST_RESERVATION_REQUEST.vehicleLicencePlate)

        // when
        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // then
        WireMock.verify(1, findVehiclesStub.getRequestPatternBuilder())
        WireMock.verify(1, addVehicleStub.getRequestPatternBuilder())
    }

    @Test
    fun `skip existing vehicle during book empty spot`() {
        // given
        val requestedSpot = 1

        val findVehiclesStub = prepareFindVehiclesStub(listOf(getVehicleDto(TEST_RESERVATION_REQUEST.vehicleLicencePlate)))
        val addVehicleStub = prepareAddVehicleStub(TEST_RESERVATION_REQUEST.vehicleLicencePlate)

        // when
        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // then
        WireMock.verify(1, findVehiclesStub.getRequestPatternBuilder())
        WireMock.verify(0, addVehicleStub.getRequestPatternBuilder())
    }

    private fun prepareFindVehiclesStub(response: List<UserVehicleDto>): StubMapping = WireMock.stubFor(
        WireMock.get(WireMock.urlPathMatching("/vehicles"))
            .withQueryParam("licencePlate", WireMock.equalTo(TEST_RESERVATION_REQUEST.vehicleLicencePlate))
            .willReturn(
                WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(response.toJson()),
            ),
    )

    private fun prepareAddVehicleStub(licencePlate: String): StubMapping = WireMock.stubFor(
        WireMock.post(WireMock.urlPathMatching("/vehicles"))
            .withRequestBody(WireMock.containing(licencePlate))
            .willReturn(
                WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(getVehicleDto(licencePlate).toJson()),
            ),
    )

    private fun getVehicleDto(licencePlate: String) = UserVehicleDto(
        id = UUID.randomUUID(),
        brand = null,
        model = null,
        licencePlate = licencePlate,
    )

    private fun StubMapping.getRequestPatternBuilder(): RequestPatternBuilder {
        val requestPatternBuilder = RequestPatternBuilder(request.method, request.urlMatcher)
        request.bodyPatterns?.forEach { requestPatternBuilder.withRequestBody(it) }
        request.headers?.forEach { (key, pattern) -> requestPatternBuilder.withHeader(key, pattern.valuePattern) }
        request.queryParameters?.forEach { (key, pattern) -> requestPatternBuilder.withQueryParam(key, pattern.valuePattern) }
        return requestPatternBuilder
    }

    companion object {
        const val URL_SPOT = "/spots"

        val TEST_RESERVATION_REQUEST = ReservationRequestDto(
            reservedFrom = ZonedDateTime.of(2024, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC),
            reservedTo = ZonedDateTime.of(2024, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC),
            vehicleLicencePlate = "AE1234OE",
        )
    }
}
