package icu.cyclone.parking.booking.infrastructure.security.service

import java.util.Date
import java.util.UUID

import javax.crypto.SecretKey

import icu.cyclone.parking.booking.infrastructure.properties.SecurityProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service

@Service
class JwtService(
    private val securityProperties: SecurityProperties,
) {
    fun isTokenValid(token: String): Boolean {
        return extractUsername(token).isNotBlank() && !isTokenExpired(token)
    }

    fun extractUsername(token: String): String {
        return extractClaim(token) { claims ->
            claims.subject
        }
    }

    fun extractUserId(token: String): UUID {
        return extractClaim(token) { claims ->
            UUID.fromString(
                claims[CLAIM_SUBJECT_ID] as String,
            )
        }
    }

    fun extractUserRole(token: String): String {
        return extractClaim(token) { claims ->
            claims[CLAIM_ROLE] as String
        }
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { it.expiration }
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(securityProperties.jwt.secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    companion object {
        const val CLAIM_SUBJECT_ID = "subjectId"
        const val CLAIM_ROLE = "subjectRole"
    }
}
