package com.example.ms_stock.dtos

import com.example.ms_stock.models.Stock
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.*

@Schema(description = "Requisição para criar um novo registro de estoque")
data class CreateStockRequest(

    @Schema(
        description = "ID do produto associado a este estoque",
        example = "550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "O productId é obrigatório.")
    val productId: UUID,

    @Schema(
        description = "Quantidade disponível em estoque",
        example = "100",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A quantidade é obrigatória.")
    @field:Min(value = 0, message = "A quantidade não pode ser negativa.")
    val availableQuantity: Long
)

@Schema(description = "Requisição para atualizar um registro de estoque existente")
data class UpdateStockRequest(

    @Schema(
        description = "Nova quantidade disponível em estoque",
        example = "150",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A quantidade é obrigatória.")
    @field:Min(value = 0, message = "A quantidade não pode ser negativa.")
    val availableQuantity: Long,

    @Schema(
        description = "Versão atual do registro (para Optimistic Locking). Deve ser exatamente igual à versão retornada na última consulta.",
        example = "0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A versão do registro é obrigatória para atualização.")
    @field:Min(value = 0)
    val version: Int
)

@Schema(description = "Resposta com os dados de um registro de estoque")
data class StockResponseDTO(

    @Schema(
        description = "ID único do registro de estoque",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val id: UUID,

    @Schema(
        description = "ID do produto associado",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val productId: UUID,

    @Schema(description = "Quantidade disponível", example = "100")
    val availableQuantity: Long,

    @Schema(
        description = "Versão atual do registro (usado para controle de concorrência)",
        example = "0"
    )
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