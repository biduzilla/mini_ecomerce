package com.example.ms_stock.dtos

import com.example.ms_stock.models.Stock
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateStockRequest(
    @field:NotNull(message = "O productId é obrigatório.")
    val productId: UUID,

    @field:NotNull(message = "A quantidade é obrigatória.")
    @field:Min(value = 0, message = "A quantidade não pode ser negativa.")
    val availableQuantity: Long
)

data class UpdateStockRequest(
    @field:NotNull(message = "A quantidade é obrigatória.")
    @field:Min(value = 0, message = "A quantidade não pode ser negativa.")
    val availableQuantity: Long,
    @field:NotNull(message = "A versão do registro é obrigatória para atualização.")
    @field:Min(value = 0)
    val version: Int
)

data class StockResponseDTO(
    val id: UUID,
    val productId: UUID,
    val availableQuantity: Long,
    val version: Int
)

fun CreateStockRequest.toModel(): Stock {
    return Stock(
        productId = productId,
        availableQuantity = availableQuantity
    )
}

fun UpdateStockRequest.toModel(currentId: UUID): Stock {
    return Stock(
        id = currentId,
        availableQuantity = availableQuantity,
        version = version
    )
}