package icu.cyclone.parking.booking.infrastructure.configuration

import icu.cyclone.parking.booking.service.ReservationDispatcherService
import icu.cyclone.parking.booking.service.ReservationDispatcherStubService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReservationDispatcherConfiguration {
    @Bean
    @ConditionalOnMissingBean(ReservationDispatcherService::class)
    fun stubReservationDispatcherService(): ReservationDispatcherService {
        return ReservationDispatcherStubService()
    }
}
