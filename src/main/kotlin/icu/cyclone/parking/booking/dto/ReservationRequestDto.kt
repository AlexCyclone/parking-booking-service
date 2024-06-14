package icu.cyclone.parking.booking.dto

import java.time.ZonedDateTime

data class ReservationRequestDto(
    val reservedFrom: ZonedDateTime,
    val reservedTo: ZonedDateTime,
    val vehicleLicencePlate: String,
)
