package icu.cyclone.parking.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@ConfigurationPropertiesScan
@EnableFeignClients
@EnableDiscoveryClient
class BookingServiceApplication

fun main(args: Array<String>) {
    runApplication<BookingServiceApplication>(*args)
}
