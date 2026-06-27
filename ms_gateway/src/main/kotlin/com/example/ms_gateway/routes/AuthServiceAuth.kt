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
class AuthServiceAuth {
    companion object {
        private const val MS_AUTH_URI = "http://localhost:8081"
        private val FALLBACK_URI = URI.create("forward:/fallbackRoute")
    }

    @Bean
    fun msAuthRoute(): RouterFunction<ServerResponse> = route("ms-auth-service")
        .route(
            path("/api/auth/**")
                .or(path("/api/user/**")), http()
        )
        .before(uri(URI.create(MS_AUTH_URI)))
        .filter(
            CircuitBreakerFilterFunctions.circuitBreaker(
                "msAuthCircuitBreaker",
                FALLBACK_URI
            )
        )
        .build()

    @Bean
    fun msAuthFallbackRoute(): RouterFunction<ServerResponse> = route("msAuthFallbackRoute")
        .route(path("/msAuthFallbackRoute")) {
            ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("O serviço de autenticação está indisponível no momento.")
        }
        .build()

    @Bean
    fun msAuthApiDocsRoute(): RouterFunction<ServerResponse> = route("ms-auth-api-docs")
        .route(path("/docs/ms-auth/v3/api-docs"), http())
        .before(uri("http://localhost:8081"))
        .filter(setPath("/v3/api-docs"))
        .build()
}