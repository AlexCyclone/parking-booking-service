package icu.cyclone.parking.booking.dto

import java.time.ZonedDateTime

data class SpotDto(
    val spotId: Int,
    val stateFrom: ZonedDateTime,
    val stateTo: ZonedDateTime?,
    val reservations: List<ReservationDto> = emptyList(),
)
