package icu.cyclone.parking.booking.service.transformer

import java.util.UUID

import icu.cyclone.parking.booking.dto.ReservationDto
import icu.cyclone.parking.booking.dto.ReservationRequestDto
import icu.cyclone.parking.booking.model.Reservation

object ReservationTransformer {
    fun Reservation.toDto(): ReservationDto = ReservationDto(
        id = checkNotNull(id) { "Unexpected null value in reservation.id" },
        spotId = spotId,
        reservedFrom = reservedFrom,
        reservedTo = reservedTo,
        userId = userId,
        vehicleLicencePlate = vehicleLicencePlate,
    )

    fun ReservationRequestDto.toNewReservation(spotId: Int, userId: UUID) = Reservation(
        spotId = spotId,
        reservedFrom = reservedFrom,
        reservedTo = reservedTo,
        userId = userId,
        vehicleLicencePlate = vehicleLicencePlate,
    )
}
