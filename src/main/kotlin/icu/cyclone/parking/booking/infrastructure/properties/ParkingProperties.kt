package icu.cyclone.parking.booking.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("parking")
data class ParkingProperties(
    val parkingName: String,
    val address: Address,
    val parameters: ParkingParameters,
)

data class Address(
    val countryCode: String,
    val city: String,
    val addressString: String?,
)

data class ParkingParameters(
    val spotCount: Int,
)
