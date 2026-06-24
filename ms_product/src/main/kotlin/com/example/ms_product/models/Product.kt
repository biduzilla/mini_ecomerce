package com.example.ms_product.models

import com.example.ms_product.dtos.ProductDTO
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "PRODUCTS")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var name: String = "",
    var price: BigDecimal = BigDecimal.ZERO,
) : BaseModel()

fun Product.toDTO(): ProductDTO =
    ProductDTO(
        id = id,
        name = name,
        price = price,
    )
