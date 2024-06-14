package icu.cyclone.parking.booking.service

import java.time.ZonedDateTime

import icu.cyclone.parking.booking.dao.ReservationDao
import icu.cyclone.parking.booking.infrastructure.properties.ParkingProperties
import icu.cyclone.parking.booking.model.Spot
import icu.cyclone.parking.booking.service.transformer.SpotTransformer.getSpot
import org.springframework.stereotype.Service

@Service
class SpotService(
    private val reservationDao: ReservationDao,
    private val parkingProperties: ParkingProperties,
) {
    fun findSpots(
        available: Boolean,
        dateFrom: ZonedDateTime,
        dateTo: ZonedDateTime?,
    ): List<Spot> {
        return if (available) {
            findAvailableSpotsByRange(dateFrom, dateTo)
        } else {
            findAllSpotsByRange(dateFrom, dateTo)
        }
    }

    private fun findAvailableSpotsByRange(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Spot> {
        return reservationDao.findAvailableSpotId(dateFrom, dateTo)
            .map { getSpot(spotId = it, stateFrom = dateFrom, stateTo = dateTo) }
    }

    private fun findAllSpotsByRange(dateFrom: ZonedDateTime, dateTo: ZonedDateTime?): List<Spot> {
        val reservationMap =
            reservationDao.findReservations(dateFrom, dateTo).groupBy { it.spotId }

        return List(parkingProperties.parameters.spotCount) {
            getSpot(
                spotId = it + 1,
                stateFrom = dateFrom,
                stateTo = dateTo,
                reservationMap = reservationMap,
            )
        }
    }
}
