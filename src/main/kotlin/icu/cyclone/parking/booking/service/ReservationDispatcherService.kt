package icu.cyclone.parking.booking.service

import icu.cyclone.parking.booking.model.Reservation

interface ReservationDispatcherService {
    fun dispatch(reservation: Reservation)
}
