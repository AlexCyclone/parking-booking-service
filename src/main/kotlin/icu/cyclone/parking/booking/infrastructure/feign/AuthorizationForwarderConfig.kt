package icu.cyclone.parking.booking.infrastructure.feign

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean

class AuthorizationForwarderConfig {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return AuthorizationForwarderInterceptor()
    }
}
