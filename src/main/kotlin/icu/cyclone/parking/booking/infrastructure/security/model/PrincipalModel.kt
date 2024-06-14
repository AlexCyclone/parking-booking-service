package icu.cyclone.parking.booking.infrastructure.security.model

import java.util.UUID

data class PrincipalModel(
    val userId: UUID,
    val userEmail: String,
)
