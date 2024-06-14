package icu.cyclone.parking.booking.infrastructure.feign

import feign.RequestInterceptor
import feign.RequestTemplate
import icu.cyclone.parking.booking.infrastructure.security.model.CredentialsModel
import org.springframework.security.core.context.SecurityContextHolder

internal class AuthorizationForwarderInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        SecurityContextHolder.getContext().authentication
            .takeIf { it != null }
            ?.credentials
            ?.let { if (it is CredentialsModel) it.token else null }
            ?.let { template.header("Authorization", "Bearer $it") }
    }
}
