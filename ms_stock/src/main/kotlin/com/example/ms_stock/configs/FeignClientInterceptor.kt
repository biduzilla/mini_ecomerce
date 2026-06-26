package com.example.ms_stock.configs

import feign.RequestInterceptor
import feign.RequestTemplate
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class FeignClientInterceptor : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes

        if (attributes != null) {
            val request: HttpServletRequest = attributes.request
            val authHeader = request.getHeader("Authorization")

            if (!authHeader.isNullOrBlank()) {
                template.header("Authorization", authHeader)
            }
        }
    }
}