package com.example.ms_product.dtos

import com.example.ms_product.models.Product
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.*

data class ProductDTO(
    var id: UUID? = null,
    @field:NotBlank(message = "Name must not be empty")
    @field:Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    var name: String = "",

    @field:NotNull(message = "Price must not be null")
    @field:DecimalMin(
        value = "0.01",
        inclusive = true,
        message = "Price must be greater than 0"
    )
    var price: BigDecimal = BigDecimal.ZERO,
)

fun ProductDTO.toModel(): Product =
    Product(
        id = id,
        name = name,
        price = price,
    )
