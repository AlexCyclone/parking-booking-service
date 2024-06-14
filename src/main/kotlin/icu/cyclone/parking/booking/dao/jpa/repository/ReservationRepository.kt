package icu.cyclone.parking.booking.dao.jpa.repository

import java.time.ZonedDateTime
import java.util.UUID

import icu.cyclone.parking.booking.dao.jpa.entity.ReservationEntity
import io.hypersistence.utils.hibernate.type.range.Range
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<ReservationEntity, UUID> {
    @Query(
        """
            SELECT * 
            FROM booking.reservations r 
            WHERE r.reservation_range && :range
        """,
        nativeQuery = true,
    )
    fun findByRangeOverlapping(range: Range<ZonedDateTime>): List<ReservationEntity>

    @Query(
        """
            SELECT generate_series(1, :spotCount) as spots 
            EXCEPT 
            SELECT spot_id 
            FROM booking.reservations r 
            WHERE r.reservation_range && :range 
            ORDER BY spots ASC
        """,
        nativeQuery = true,
    )
    fun findAvailableSpotIdByRange(spotCount: Int, range: Range<ZonedDateTime>): List<Int>
}
