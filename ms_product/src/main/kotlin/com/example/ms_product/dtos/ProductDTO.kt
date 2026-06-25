package com.example.ms_product.dtos

import com.example.ms_product.models.Product
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.*

@Schema(description = "Representação de um produto")
data class ProductDTO(

    @Schema(
        description = "ID único do produto (gerado pelo servidor)",
        example = "550e8400-e29b-41d4-a716-446655440000",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    val id: UUID? = null,

    @Schema(
        description = "Nome do produto",
        example = "Notebook Dell Inspiron 15",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank(message = "Name must not be empty")
    @field:Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    val name: String = "",

    @Schema(
        description = "Preço do produto em reais",
        example = "3499.90",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "Price must not be null")
    @field:DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    val price: BigDecimal = BigDecimal.ZERO,
)

fun ProductDTO.toModel(): Product = Product(
    id = id,
    name = name,
    price = price,
)