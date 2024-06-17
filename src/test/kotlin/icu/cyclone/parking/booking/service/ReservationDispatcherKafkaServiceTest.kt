package icu.cyclone.parking.booking.service

import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.CompletableFuture

import icu.cyclone.parking.booking.infrastructure.properties.KafkaProperties
import icu.cyclone.parking.booking.model.Reservation
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult

@ExtendWith(MockKExtension::class)
class ReservationDispatcherKafkaServiceTest {

    @MockK
    lateinit var kafkaProperties: KafkaProperties

    @MockK
    lateinit var kafkaTemplate: KafkaTemplate<String, Reservation>

    @InjectMockKs
    lateinit var reservationDispatcherKafkaService: ReservationDispatcherKafkaService

    @Test
    fun dispatch() {
        every { kafkaProperties.reservationTopic.name } returns "topic"
        every { kafkaTemplate.send("topic", any()) } returns mockk<CompletableFuture<SendResult<String, Reservation>>>()

        reservationDispatcherKafkaService.dispatch(
            Reservation(
                id = UUID.randomUUID(),
                spotId = 123,
                reservedFrom = ZonedDateTime.now(),
                reservedTo = ZonedDateTime.now(),
                userId = UUID.randomUUID(),
                vehicleLicencePlate = "AAAA",
            ),
        )

        verify(exactly = 1) { kafkaTemplate.send("topic", any()) }
    }
}
