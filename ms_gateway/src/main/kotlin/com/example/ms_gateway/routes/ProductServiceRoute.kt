package com.example.ms_gateway.routes

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

@Configuration
class ProductServiceRoute {
    companion object {
        private const val MS_PRODUCT_URI = "http://localhost:8083"
        private val FALLBACK_URI = URI.create("forward:/msProductFallbackRoute")
    }

    @Bean
    fun msProductRoute(): RouterFunction<ServerResponse> = route("ms-product-service")
        .route(path("/api/products/**"), http())
        .before(uri(URI.create(MS_PRODUCT_URI)))
        .filter(
            CircuitBreakerFilterFunctions.circuitBreaker(
                "msProductCircuitBreaker",
                FALLBACK_URI
            )
        )
        .build()

    @Bean
    fun msProductFallbackRoute(): RouterFunction<ServerResponse> = route("msProductFallbackRoute")
        .route(path("/msProductFallbackRoute")) { _ ->
            ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("O serviço de produtos está indisponível no momento.")
        }
        .build()

    @Bean
    fun msProductApiDocsRoute(): RouterFunction<ServerResponse> = route("ms-product-api-docs")
        .route(path("/docs/ms-product/v3/api-docs"), http())
        .before(uri(URI.create(MS_PRODUCT_URI)))
        .filter(setPath("/api-docs"))
        .build()
}