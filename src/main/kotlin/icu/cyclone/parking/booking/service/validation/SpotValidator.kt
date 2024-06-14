package icu.cyclone.parking.booking.service.validation

import icu.cyclone.parking.booking.infrastructure.exception.BadRequestException
import icu.cyclone.parking.booking.infrastructure.properties.ParkingProperties
import org.springframework.stereotype.Component

@Component
class SpotValidator(
    private val parkingProperties: ParkingProperties,
) {
    fun validateSpotId(spotId: Int) {
        if (spotId < 1 || spotId > parkingProperties.parameters.spotCount) {
            throw BadRequestException("Spot with id $spotId not exists")
        }
    }
}
