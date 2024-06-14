package icu.cyclone.parking.booking.dao.jpa

import java.time.ZonedDateTime

import icu.cyclone.parking.booking.dao.ReservationDao
import icu.cyclone.parking.booking.dao.jpa.repository.ReservationRepository
import icu.cyclone.parking.booking.dao.jpa.transformer.ReservationEntityTransformer.toEntity
import icu.cyclone.parking.booking.dao.jpa.transformer.ReservationEntityTransformer.toReservation
import icu.cyclone.parking.booking.infrastructure.exception.ConflictException
import icu.cyclone.parking.booking.infrastructure.properties.ParkingProperties
import icu.cyclone.parking.booking.model.Reservation
import io.hypersistence.utils.hibernate.type.range.Range
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class JpaReservationDao(
    private val reservationRepository: ReservationRepository,
    private val parkingProperties: ParkingProperties,
) : ReservationDao {
    override fun save(reservation: Reservation): Reservation = try {
        reservationRepository.save(
            reservation.toEntity(),
        ).toReservation()
    } catch (e: DataIntegrityViolationException) {
        throw ConflictException("Reservation conflict on spot ${reservation.spotId}", e)
    }

    override fun findAvailableSpotId(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Int> =
        reservationRepository.findAvailableSpotIdByRange(
            parkingProperties.parameters.spotCount,
            dateTo?.let { Range.openClosed(dateFrom, dateTo) } ?: Range.closedInfinite(dateFrom),
        )

    override fun findReservations(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Reservation> =
        reservationRepository.findByRangeOverlapping(
            dateTo?.let { Range.openClosed(dateFrom, dateTo) } ?: Range.closedInfinite(dateFrom),
        ).map { it.toReservation() }
}
