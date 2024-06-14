package icu.cyclone.parking.booking.infrastructure.configuration

import icu.cyclone.parking.booking.infrastructure.properties.SecurityProperties
import icu.cyclone.parking.booking.infrastructure.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val securityProperties: SecurityProperties,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { it.disable() }
            .authorizeHttpRequests { request ->
                securityProperties.openEndpoints.forEach { pattern ->
                    request.requestMatchers(pattern).permitAll()
                }
                request.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    companion object {
        const val ROLE_USER = "ROLE_USER"
        const val ROLE_ADMIN = "ROLE_ADMIN"
    }
}
