package icu.cyclone.parking.booking.dao

import java.time.ZonedDateTime

import icu.cyclone.parking.booking.model.Reservation

interface ReservationDao {
    fun save(reservation: Reservation): Reservation

    fun findAvailableSpotId(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Int>

    fun findReservations(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Reservation>
}
