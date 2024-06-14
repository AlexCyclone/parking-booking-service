package icu.cyclone.parking.booking.infrastructure.exception

class ForbiddenException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
