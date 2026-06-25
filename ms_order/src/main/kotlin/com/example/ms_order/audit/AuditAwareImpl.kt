package com.example.ms_order.audit

import com.example.ms_order.security.AuthenticatedUser
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component("auditAwareImpl")
class AuditAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.isAuthenticated && authentication.principal is AuthenticatedUser) {
            val userAuth = authentication.principal as AuthenticatedUser
            Optional.of(userAuth.userId.toString())
        } else {
            Optional.empty()
        }
    }
}