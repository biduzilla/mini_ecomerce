package com.example.ms_stock.configs

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class FeignClientInterceptor : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.isAuthenticated) {
            val details = authentication.details
            if (details is String && details.startsWith("Bearer ")) {
                template.header("Authorization", details)
            }
        }
    }
}