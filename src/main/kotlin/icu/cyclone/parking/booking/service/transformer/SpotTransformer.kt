package icu.cyclone.parking.booking.service.transformer

import java.time.ZonedDateTime

import icu.cyclone.parking.booking.dto.SpotDto
import icu.cyclone.parking.booking.model.Reservation
import icu.cyclone.parking.booking.model.Spot
import icu.cyclone.parking.booking.service.transformer.ReservationTransformer.toDto

object SpotTransformer {
    fun Spot.toDto(): SpotDto = SpotDto(
        spotId = spotId,
        stateFrom = stateFrom,
        stateTo = stateTo,
        reservations = reservations.map { it.toDto() },
    )

    fun getSpot(
        spotId: Int,
        stateFrom: ZonedDateTime,
        stateTo: ZonedDateTime?,
        reservationMap: Map<Int, List<Reservation>> = emptyMap(),
    ) = Spot(
        spotId = spotId,
        stateFrom = stateFrom,
        stateTo = stateTo,
        reservations = reservationMap.getOrElse(spotId) { emptyList() },
    )
}
