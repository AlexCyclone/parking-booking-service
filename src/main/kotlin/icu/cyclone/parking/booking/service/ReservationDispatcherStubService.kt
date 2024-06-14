package icu.cyclone.parking.booking.service

import icu.cyclone.parking.booking.model.Reservation
import org.slf4j.LoggerFactory

class ReservationDispatcherStubService : ReservationDispatcherService {
    override fun dispatch(reservation: Reservation) {
        logger.info("Do Nothing. userId: [{${reservation.userId}}] vehicleLicencePlate: [${reservation.vehicleLicencePlate}]")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
