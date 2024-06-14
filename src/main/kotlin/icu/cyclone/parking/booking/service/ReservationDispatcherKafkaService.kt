package icu.cyclone.parking.booking.service

import icu.cyclone.parking.booking.infrastructure.properties.KafkaProperties
import icu.cyclone.parking.booking.model.Reservation
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
@Profile("kafka-mode")
class ReservationDispatcherKafkaService(
    private val kafkaTemplate: KafkaTemplate<String, Reservation>,
    private val kafkaProperties: KafkaProperties,
) : ReservationDispatcherService {
    override fun dispatch(reservation: Reservation) {
        runCatching {
            kafkaTemplate.send(kafkaProperties.reservationTopic.name, reservation)
            logger.info("Reservation message sent: [$reservation]")
        }.onFailure {
            logger.error(it.message, it)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
