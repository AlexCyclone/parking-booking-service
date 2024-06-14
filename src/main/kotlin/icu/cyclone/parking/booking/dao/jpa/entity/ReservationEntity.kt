package icu.cyclone.parking.booking.dao.jpa.entity

import java.time.ZonedDateTime
import java.util.UUID

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType
import io.hypersistence.utils.hibernate.type.range.Range
import org.hibernate.annotations.Type

@Entity
@Table(name = "reservations")
data class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val spotId: Int? = null,
    @Type(PostgreSQLRangeType::class)
    val reservationRange: Range<ZonedDateTime>? = null,
    val userId: UUID? = null,
    val vehicleLicencePlate: String? = null,
)
