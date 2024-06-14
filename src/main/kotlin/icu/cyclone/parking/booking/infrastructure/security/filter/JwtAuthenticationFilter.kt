package icu.cyclone.parking.booking.infrastructure.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import icu.cyclone.parking.booking.infrastructure.security.model.CredentialsModel
import icu.cyclone.parking.booking.infrastructure.security.model.PrincipalModel
import icu.cyclone.parking.booking.infrastructure.security.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader(AUTH_HEADER)
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val token = authHeader.substring(BEARER_PREFIX.length)
            if (SecurityContextHolder.getContext().authentication == null && jwtService.isTokenValid(token)) {
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(
                        PrincipalModel(
                            userId = jwtService.extractUserId(token),
                            userEmail = jwtService.extractUsername(token),
                        ),
                        CredentialsModel(token),
                        listOf(SimpleGrantedAuthority(jwtService.extractUserRole(token))),
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }
            }
        }.onFailure {
            logger.warn(it.message, it)
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        const val AUTH_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}
