package icu.cyclone.parking.booking.service

import java.util.UUID

import icu.cyclone.parking.booking.dao.ReservationDao
import icu.cyclone.parking.booking.dto.ReservationRequestDto
import icu.cyclone.parking.booking.model.Reservation
import icu.cyclone.parking.booking.service.transformer.ReservationTransformer.toNewReservation
import icu.cyclone.parking.booking.service.validation.SpotValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationDao: ReservationDao,
    private val spotValidator: SpotValidator,
    private val reservationDispatcherService: ReservationDispatcherService,
) {
    fun addReservation(
        userId: UUID,
        spotId: Int,
        reservationRequest: ReservationRequestDto,
    ): Reservation {
        spotValidator.validateSpotId(spotId)
        return reservationDao.save(reservationRequest.toNewReservation(spotId, userId))
            .also {
                logger.info("Reservation created: [$it]")
                reservationDispatcherService.dispatch(it)
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
