package icu.cyclone.parking.booking.api

import java.time.ZonedDateTime

import icu.cyclone.parking.booking.dto.ReservationDto
import icu.cyclone.parking.booking.dto.ReservationRequestDto
import icu.cyclone.parking.booking.dto.SpotDto
import icu.cyclone.parking.booking.infrastructure.configuration.SecurityConfiguration.Companion.ROLE_ADMIN
import icu.cyclone.parking.booking.infrastructure.configuration.SecurityConfiguration.Companion.ROLE_USER
import icu.cyclone.parking.booking.infrastructure.security.service.AuthInfoService
import icu.cyclone.parking.booking.service.ReservationService
import icu.cyclone.parking.booking.service.SpotService
import icu.cyclone.parking.booking.service.transformer.ReservationTransformer.toDto
import icu.cyclone.parking.booking.service.transformer.SpotTransformer.toDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/spots"])
@Tag(name = "Parking Spots API")
class SpotController(
    private val reservationService: ReservationService,
    private val spotService: SpotService,
    private val authInfoService: AuthInfoService,
) {
    @PreAuthorize("hasAnyRole('$ROLE_USER', '$ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "", description = "Find spots that meet the specified criteria")
    fun findSpots(
        @RequestParam dateFrom: ZonedDateTime = ZonedDateTime.now(),
        @RequestParam dateTo: ZonedDateTime?,
        @RequestParam available: Boolean = true,
    ): List<SpotDto> {
        return spotService.findSpots(
            dateFrom = dateFrom,
            dateTo = dateTo,
            available = available,
        ).map { it.toDto() }
    }

    @PreAuthorize("hasRole('$ROLE_USER')")
    @PostMapping(path = ["{spotId}/reservation"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "", description = "Reservation of a parking spot for a specific car")
    fun bookSpot(
        @PathVariable spotId: Int,
        @RequestBody reservationRequest: ReservationRequestDto,
    ): ReservationDto {
        return reservationService.addReservation(
            userId = authInfoService.getAuthenticatedUserId(),
            spotId = spotId,
            reservationRequest = reservationRequest,
        ).toDto()
    }
}
