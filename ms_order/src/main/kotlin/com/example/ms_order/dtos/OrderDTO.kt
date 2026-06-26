package com.example.ms_order.dtos

import com.example.ms_order.enums.OrderStatusEnum
import com.example.ms_order.models.Order
import com.example.ms_order.models.OrderItem
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Requisição para criar um novo pedido")
data class CreateOrderRequest(

    @Schema(
        description = "Lista de itens do pedido",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotEmpty(message = "O pedido deve conter pelo menos um item")
    @field:Valid
    val items: List<CreateOrderItemRequest>
)

@Schema(description = "Item do pedido a ser criado")
data class CreateOrderItemRequest(

    @Schema(
        description = "ID do produto",
        example = "550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "O productId é obrigatório")
    val productId: UUID,

    @Schema(
        description = "Quantidade do produto",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "A quantidade é obrigatória")
    @field:Min(value = 1, message = "A quantidade deve ser maior que zero")
    val quantity: Int,

    @Schema(
        description = "Preço unitário do produto",
        example = "99.90",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "O preço unitário é obrigatório")
    @field:DecimalMin(value = "0.01", inclusive = true, message = "O preço deve ser maior que zero")
    val unitPrice: BigDecimal
)

@Schema(description = "Resposta com os dados do pedido")
data class OrderResponseDTO(

    @Schema(
        description = "ID único do pedido",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val id: UUID,

    @Schema(
        description = "ID do cliente que criou o pedido",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    val customerId: UUID,

    @Schema(description = "Lista de itens do pedido")
    val items: List<OrderItemResponseDTO>,

    @Schema(
        description = "Valor total do pedido",
        example = "199.80"
    )
    val totalAmount: BigDecimal,

    @Schema(
        description = "Status atual do pedido",
        example = "PENDING"
    )
    val status: OrderStatusEnum,

    @Schema(
        description = "Data de criação do pedido",
        example = "2026-06-25T14:30:00"
    )
    val createdAt: LocalDateTime
)

@Schema(description = "Resposta com os dados de um item do pedido")
data class OrderItemResponseDTO(

    @Schema(description = "ID do item", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: UUID,

    @Schema(description = "ID do produto", example = "550e8400-e29b-41d4-a716-446655440000")
    val productId: UUID,

    @Schema(description = "Quantidade", example = "2")
    val quantity: Int,

    @Schema(description = "Preço unitário", example = "99.90")
    val unitPrice: BigDecimal,

    @Schema(description = "Preço total do item", example = "199.80")
    val totalPrice: BigDecimal
)


fun CreateOrderRequest.toModel(): Order {
    val order = Order(
        status = OrderStatusEnum.PENDING
    )

    order.items = items.map { itemRequest ->
        OrderItem(
            productId = itemRequest.productId,
            quantity = itemRequest.quantity,
            unitPrice = itemRequest.unitPrice,
            totalPrice = itemRequest.unitPrice.multiply(BigDecimal(itemRequest.quantity)),
            order = order
        )
    }.toMutableList()

    return order
}

fun Order.toResponseDTO(): OrderResponseDTO {
    return OrderResponseDTO(
        id = id!!,
        customerId = UUID.fromString(createdBy),
        items = items.map { it.toResponseDTO() },
        totalAmount = totalAmount,
        status = status,
        createdAt = createdAt
    )
}

fun OrderItem.toResponseDTO(): OrderItemResponseDTO {
    return OrderItemResponseDTO(
        id = id!!,
        productId = productId!!,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice
    )
}