package com.example.ms_order.configs

import com.example.ms_order.producers.OrderEventProducers
import feign.RequestInterceptor
import feign.RequestTemplate
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class FeignClientInterceptor : RequestInterceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(OrderEventProducers::class.java)
    }

    override fun apply(template: RequestTemplate) {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        logger.info("FeignClientInterceptor")
        if (attributes != null) {
            val request: HttpServletRequest = attributes.request
            val authHeader = request.getHeader("Authorization")
            logger.info("FeignClientInterceptor - authHeader $authHeader")
            if (!authHeader.isNullOrBlank()) {
                template.header("Authorization", authHeader)
            }
        }
    }
}