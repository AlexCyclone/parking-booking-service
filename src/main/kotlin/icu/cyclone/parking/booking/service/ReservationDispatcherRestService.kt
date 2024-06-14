package icu.cyclone.parking.booking.service

import icu.cyclone.parking.booking.client.vehicle.UserVehicleClient
import icu.cyclone.parking.booking.client.vehicle.dto.VehicleRequest
import icu.cyclone.parking.booking.model.Reservation
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("rest-mode")
class ReservationDispatcherRestService(
    private val userVehicleClient: UserVehicleClient,
) : ReservationDispatcherService {
    override fun dispatch(reservation: Reservation) {
        runCatching {
            userVehicleClient.findUserVehicles(reservation.vehicleLicencePlate).onEach {
                logger.info("User vehicle already exist. userId: [{${reservation.userId}}] vehicleLicencePlate: [${it.licencePlate}]")
            }.firstOrNull()
                ?: userVehicleClient.addUserVehicles(VehicleRequest(reservation.vehicleLicencePlate)).also {
                    logger.info("New user vehicle added. userId: [{${reservation.userId}}] vehicleLicencePlate: [${it.licencePlate}]")
                }
        }.onFailure {
            logger.error(it.message, it)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
