package icu.cyclone.parking.booking.dto

import java.time.ZonedDateTime
import java.util.UUID

data class ReservationDto(
    val id: UUID,
    val spotId: Int,
    val reservedFrom: ZonedDateTime,
    val reservedTo: ZonedDateTime,
    val userId: UUID,
    val vehicleLicencePlate: String,
)
