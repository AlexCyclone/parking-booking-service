package icu.cyclone.parking.booking.client.vehicle.dto

import java.util.UUID

data class UserVehicleDto(
    val id: UUID,
    val brand: String?,
    val model: String?,
    val licencePlate: String,
)
