package com.example.ms_order.configs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Order Microservice API")
                    .version("1.0.0")
                    .description("API para gerenciamento de pedidos com validação de estoque e eventos Kafka")
                    .contact(
                        Contact()
                            .name("Time de Engenharia")
                            .email("eng@example.com")
                    )
                    .license(
                        License().name("MIT").url("https://opensource.org/licenses/MIT")
                    )
            )
//         .components(
//             Components().addSecuritySchemes(
//                 "BearerAuth",
//                 SecurityScheme()
//                     .type(SecurityScheme.Type.HTTP)
//                     .scheme("bearer")
//                     .bearerFormat("JWT")
//             )
//         )
    }
}