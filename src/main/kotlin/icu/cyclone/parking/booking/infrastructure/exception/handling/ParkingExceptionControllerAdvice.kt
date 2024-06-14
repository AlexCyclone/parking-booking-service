package icu.cyclone.parking.booking.infrastructure.exception.handling

import icu.cyclone.parking.booking.infrastructure.exception.BadRequestException
import icu.cyclone.parking.booking.infrastructure.exception.ConflictException
import icu.cyclone.parking.booking.infrastructure.exception.ForbiddenException
import icu.cyclone.parking.booking.infrastructure.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ParkingExceptionControllerAdvice {
    @ExceptionHandler(AccessDeniedException::class)
    @ResponseBody
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> =
        buildErrorResponseWithLogging(e, HttpStatus.FORBIDDEN)

    @ExceptionHandler(HttpMessageConversionException::class)
    @ResponseBody
    fun handleHttpMessageConversionException(e: HttpMessageConversionException): ResponseEntity<ErrorResponse> =
        buildErrorResponseWithLogging(e, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(BadRequestException::class)
    @ResponseBody
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ErrorResponse> =
        buildErrorResponseWithLogging(e, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(ConflictException::class)
    @ResponseBody
    fun handleConflictException(e: ConflictException): ResponseEntity<ErrorResponse> = buildErrorResponseWithLogging(e, HttpStatus.CONFLICT)

    @ExceptionHandler(ForbiddenException::class)
    @ResponseBody
    fun handleForbiddenException(e: ForbiddenException): ResponseEntity<ErrorResponse> =
        buildErrorResponseWithLogging(e, HttpStatus.FORBIDDEN)

    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ErrorResponse> =
        buildErrorResponseWithLogging(e, HttpStatus.FORBIDDEN)

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> = buildErrorResponseWithLogging(e, HttpStatus.INTERNAL_SERVER_ERROR)

    private fun buildErrorResponseWithLogging(e: Exception, httpStatus: HttpStatus): ResponseEntity<ErrorResponse> {
        val message = e.message ?: httpStatus.name
        logger.error(message, e)
        return ResponseEntity(ErrorResponse(message), httpStatus)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
