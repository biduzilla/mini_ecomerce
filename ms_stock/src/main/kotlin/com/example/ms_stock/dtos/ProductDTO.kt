package com.example.ms_stock.dtos

import java.math.BigDecimal
import java.util.*

data class ProductDTO(
    var id: UUID? = null,
    var name: String = "",
    var price: BigDecimal = BigDecimal.ZERO,
)

