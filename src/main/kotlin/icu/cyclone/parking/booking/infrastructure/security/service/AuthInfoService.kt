package icu.cyclone.parking.booking.infrastructure.security.service

import java.util.UUID

import icu.cyclone.parking.booking.infrastructure.exception.ForbiddenException
import icu.cyclone.parking.booking.infrastructure.security.model.CredentialsModel
import icu.cyclone.parking.booking.infrastructure.security.model.PrincipalModel
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthInfoService {
    fun getAuthenticatedUserId(): UUID = getAuthenticationPrincipal().userId

    fun getAuthenticatedUserEmail(): String = getAuthenticationPrincipal().userEmail

    fun getAuthenticationToken(): String = getAuthenticationCredentials().token

    private fun getAuthenticationPrincipal(): PrincipalModel {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        if (principal == null || principal !is PrincipalModel) {
            throw ForbiddenException("Authorization failed")
        } else {
            return principal
        }
    }

    private fun getAuthenticationCredentials(): CredentialsModel {
        val credentials = SecurityContextHolder.getContext().authentication?.credentials
        if (credentials == null || credentials !is CredentialsModel) {
            throw ForbiddenException("Authorization failed")
        } else {
            return credentials
        }
    }
}
