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
class StockRoutes {

    companion object {
        private const val MS_STOCK_URI = "http://localhost:8084"
        private val FALLBACK_URI = URI.create("forward:/msStockFallbackRoute")
    }

    @Bean
    fun msStockRoute(): RouterFunction<ServerResponse> = route("ms-stock-service")
        .route(path("/api/stocks/**"), http())
        .before(uri(URI.create(MS_STOCK_URI)))
        .filter(
            CircuitBreakerFilterFunctions.circuitBreaker(
                "msStockCircuitBreaker",
                FALLBACK_URI
            )
        )
        .build()

    @Bean
    fun msStockFallbackRoute(): RouterFunction<ServerResponse> = route("msStockFallbackRoute")
        .route(path("/msStockFallbackRoute")) { _ ->
            ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("O serviço de estoque está temporariamente indisponível.")
        }
        .build()

    @Bean
    fun msStockApiDocsRoute(): RouterFunction<ServerResponse> = route("ms-stock-api-docs")
        .route(path("/docs/ms-stock/v3/api-docs"), http())
        .before(uri(URI.create(MS_STOCK_URI)))
        .filter(setPath("/api-docs"))
        .build()
}