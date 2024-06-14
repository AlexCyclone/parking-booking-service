package icu.cyclone.parking.booking.model

import java.time.ZonedDateTime

data class Spot(
    val spotId: Int,
    val stateFrom: ZonedDateTime,
    val stateTo: ZonedDateTime?,
    val reservations: List<Reservation> = emptyList(),
)
