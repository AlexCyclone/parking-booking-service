package icu.cyclone.parking.booking.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile

@ConfigurationProperties("kafka")
@Profile("kafka-mode")
data class KafkaProperties(
    val reservationTopic: Topic,
)

data class Topic(
    val name: String,
    val partitionsCount: Int,
    val replicationFactor: Short,
)
