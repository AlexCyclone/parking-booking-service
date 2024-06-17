package icu.cyclone.parking.booking.api

import java.time.ZoneOffset
import java.time.ZonedDateTime

import icu.cyclone.parking.booking.IntegrationTestParent
import icu.cyclone.parking.booking.dao.jpa.repository.ReservationRepository
import icu.cyclone.parking.booking.dto.ReservationDto
import icu.cyclone.parking.booking.dto.ReservationRequestDto
import icu.cyclone.parking.booking.dto.SpotDto
import icu.cyclone.parking.booking.infrastructure.exception.handling.ErrorResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class SpotControllerIntegration : IntegrationTestParent() {

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @AfterEach
    fun cleanDb() {
        reservationRepository.deleteAll()
    }

    @Test
    fun `find all spots`() {
        // given
        val requestedSpot = 1
        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // when
        val result = performGet(
            url = URL_SPOT,
            requestParams = mapOf(
                "dateFrom" to TEST_RESERVATION_REQUEST.reservedFrom.minusHours(1),
                "dateTo" to TEST_RESERVATION_REQUEST.reservedTo.minusMinutes(30),
                "available" to false,
            ),
            expectedStatus = HttpStatus.OK,
        ).toDtoList(SpotDto::class.java)

        // then
        assertEquals(10, result.count())
        val reservedSpots = result.filterNot { it.reservations.isEmpty() }
        assertEquals(1, reservedSpots.count())
        assertEquals(requestedSpot, reservedSpots.first().spotId)
    }

    @Test
    fun `find available spots`() {
        // given
        val requestedSpot = 1
        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // when
        val result = performGet(
            url = URL_SPOT,
            requestParams = mapOf(
                "dateFrom" to TEST_RESERVATION_REQUEST.reservedFrom.minusHours(1),
            ),
            expectedStatus = HttpStatus.OK,
        ).toDtoList(SpotDto::class.java)

        // then
        assertEquals(9, result.count())
        assertTrue(result.filterNot { it.reservations.isEmpty() }.isEmpty())
        assertTrue(result.none { it.spotId == requestedSpot })
    }

    @Test
    fun `book empty spot success`() {
        // given
        val requestedSpot = 1

        // when
        val result = performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // then
        val expectedResult = ReservationDto(
            id = result.id,
            spotId = requestedSpot,
            reservedFrom = TEST_RESERVATION_REQUEST.reservedFrom,
            reservedTo = TEST_RESERVATION_REQUEST.reservedTo,
            userId = TEST_USER_ID,
            vehicleLicencePlate = TEST_RESERVATION_REQUEST.vehicleLicencePlate,
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `book next time for empty spot success`() {
        // given
        val requestedSpot = 1
        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        )

        val nextTimeRequest = TEST_RESERVATION_REQUEST.copy(
            reservedFrom = TEST_RESERVATION_REQUEST.reservedTo,
            reservedTo = TEST_RESERVATION_REQUEST.reservedTo.plusHours(1),
        )

        // when
        val result = performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = nextTimeRequest,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // then
        val expectedResult = ReservationDto(
            id = result.id,
            spotId = requestedSpot,
            reservedFrom = nextTimeRequest.reservedFrom,
            reservedTo = nextTimeRequest.reservedTo,
            userId = TEST_USER_ID,
            vehicleLicencePlate = TEST_RESERVATION_REQUEST.vehicleLicencePlate,
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `book 2 different spots success`() {
        // given
        val requestedSpot1 = 1
        val requestedSpot2 = 2

        // when
        val result1 = performPost(
            url = "$URL_SPOT/$requestedSpot1/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)
        val result2 = performPost(
            url = "$URL_SPOT/$requestedSpot2/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // then
        val expectedResult1 = ReservationDto(
            id = result1.id,
            spotId = requestedSpot1,
            reservedFrom = TEST_RESERVATION_REQUEST.reservedFrom,
            reservedTo = TEST_RESERVATION_REQUEST.reservedTo,
            userId = TEST_USER_ID,
            vehicleLicencePlate = TEST_RESERVATION_REQUEST.vehicleLicencePlate,
        )
        val expectedResult2 = ReservationDto(
            id = result2.id,
            spotId = requestedSpot2,
            reservedFrom = TEST_RESERVATION_REQUEST.reservedFrom,
            reservedTo = TEST_RESERVATION_REQUEST.reservedTo,
            userId = TEST_USER_ID,
            vehicleLicencePlate = TEST_RESERVATION_REQUEST.vehicleLicencePlate,
        )

        assertEquals(expectedResult1, result1)
        assertEquals(expectedResult2, result2)
    }

    @Test
    fun `book spot 2 times failed`() {
        // given
        val requestedSpot = 1

        performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.OK,
        ).toDto(ReservationDto::class.java)

        // when
        val error = performPost(
            url = "$URL_SPOT/$requestedSpot/reservation",
            content = TEST_RESERVATION_REQUEST,
            expectedStatus = HttpStatus.CONFLICT,
        ).toDto(ErrorResponse::class.java)

        // then

        assertEquals("Reservation conflict on spot $requestedSpot", error.message)
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
