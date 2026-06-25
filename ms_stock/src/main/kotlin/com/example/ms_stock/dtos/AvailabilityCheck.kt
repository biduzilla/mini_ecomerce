package com.example.ms_stock.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Schema(description = "Requisição para verificar disponibilidade de estoque de múltiplos itens")
data class AvailabilityCheckRequest(

    @Schema(
        description = "Lista de itens a serem verificados",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A lista de itens é obrigatória.")
    @field:Size(min = 1, message = "A lista de itens deve conter pelo menos um item.")
    @field:Valid
    val items: List<ItemRequest>
)

@Schema(description = "Item individual para verificação de disponibilidade")
data class ItemRequest(

    @Schema(
        description = "ID do produto",
        example = "550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "O productId é obrigatório.")
    val productId: UUID,

    @Schema(
        description = "Quantidade solicitada",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A quantidade é obrigatória.")
    @field:Min(value = 1, message = "A quantidade deve ser pelo menos 1.")
    val quantity: Long
)

@Schema(description = "Resposta da verificação de disponibilidade")
data class AvailabilityCheckResponse(

    @Schema(
        description = "Indica se todos os itens estão disponíveis (true) ou se algum item está indisponível (false)",
        example = "true"
    )
    val available: Boolean,

    @Schema(
        description = "Detalhes dos itens que não possuem estoque suficiente. Lista vazia se available = true."
    )
    val details: List<ItemAvailabilityDetail>
)

@Schema(description = "Detalhe de um item que está indisponível")
data class ItemAvailabilityDetail(

    @Schema(
        description = "ID do produto indisponível",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val productId: UUID,

    @Schema(
        description = "Quantidade solicitada",
        example = "2"
    )
    val requested: Long,

    @Schema(
        description = "Quantidade disponível em estoque",
        example = "1"
    )
    val available: Long
)