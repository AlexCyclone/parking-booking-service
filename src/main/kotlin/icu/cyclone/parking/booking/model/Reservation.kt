package icu.cyclone.parking.booking.model

import java.time.ZonedDateTime
import java.util.UUID

data class Reservation(
    val id: UUID? = null,
    val spotId: Int,
    val reservedFrom: ZonedDateTime,
    val reservedTo: ZonedDateTime,
    val userId: UUID,
    val vehicleLicencePlate: String,
)
