package icu.cyclone.parking.booking.client.vehicle

import icu.cyclone.parking.booking.client.vehicle.dto.UserVehicleDto
import icu.cyclone.parking.booking.client.vehicle.dto.VehicleRequest
import icu.cyclone.parking.booking.infrastructure.feign.AuthorizationForwarderConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "user-service",
    configuration = [AuthorizationForwarderConfig::class],
)
@Profile("rest-mode")
interface UserVehicleClient {
    @GetMapping("/vehicles")
    fun findUserVehicles(
        @RequestParam licencePlate: String?,
    ): List<UserVehicleDto>

    @PostMapping("/vehicles")
    fun addUserVehicles(
        @RequestBody vehicleRequest: VehicleRequest,
    ): UserVehicleDto
}
