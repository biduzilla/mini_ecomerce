package com.example.ms_gateway.routes

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.RequestPredicates.path
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import java.net.URI

@Configuration
class OrderRoutes {

    companion object {
        private const val MS_ORDER_URI = "http://localhost:8082"
        private val FALLBACK_URI = URI.create("forward:/msOrderFallbackRoute")
    }

    @Bean
    fun msOrderRoute(): RouterFunction<ServerResponse> = route("ms-order-service")
        .route(path("/api/orders/**"), http())
        .before(uri(URI.create(MS_ORDER_URI)))
        .filter(
            CircuitBreakerFilterFunctions.circuitBreaker(
                "msOrderCircuitBreaker",
                FALLBACK_URI
            )
        )
        .build()

    @Bean
    fun msOrderFallbackRoute(): RouterFunction<ServerResponse> = route("msOrderFallbackRoute")
        .route(path("/msOrderFallbackRoute")) { _ ->
            ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("O serviço de pedidos está temporariamente indisponível.")
        }
        .build()

    @Bean
    fun msOrderApiDocsRoute(): RouterFunction<ServerResponse> = route("ms-order-api-docs")
        .route(path("/docs/ms-order/v3/api-docs"), http())
        .before(uri(URI.create(MS_ORDER_URI)))
        .filter(setPath("/api-docs"))
        .build()
}