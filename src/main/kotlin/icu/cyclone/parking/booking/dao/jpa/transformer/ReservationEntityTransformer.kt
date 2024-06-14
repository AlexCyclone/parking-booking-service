package icu.cyclone.parking.booking.dao.jpa.transformer

import icu.cyclone.parking.booking.dao.jpa.entity.ReservationEntity
import icu.cyclone.parking.booking.model.Reservation
import io.hypersistence.utils.hibernate.type.range.Range

object ReservationEntityTransformer {
    fun Reservation.toEntity(): ReservationEntity = ReservationEntity(
        id = id,
        spotId = spotId,
        reservationRange = Range.openClosed(reservedFrom, reservedTo),
        userId = userId,
        vehicleLicencePlate = vehicleLicencePlate,
    )

    fun ReservationEntity.toReservation(): Reservation = Reservation(
        id = id,
        spotId = checkNotNull(spotId) { "null spotId" },
        reservedFrom = checkNotNull(reservationRange?.lower()) { "null reservedFrom" },
        reservedTo = checkNotNull(reservationRange?.upper()) { "null reservedTo" },
        userId = checkNotNull(userId) { "null userId" },
        vehicleLicencePlate = checkNotNull(vehicleLicencePlate) { "null vehicleLicencePlate" },
    )
}
