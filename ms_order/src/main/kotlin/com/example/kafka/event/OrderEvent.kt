package com.example.kafka.event

import com.example.ms_order.enums.OrderStatusEnum
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class OrderCreatedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val orderId: UUID=UUID.randomUUID(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    var status: OrderStatusEnum = OrderStatusEnum.PENDING,
    var totalAmount: BigDecimal = BigDecimal.ZERO,
    var items: List<OrderItemEvent> = listOf(),
)

data class OrderItemEvent(
    var id: UUID=UUID.randomUUID(),
    var productId: UUID? = null,
    var quantity: Int = 1,
    var unitPrice: BigDecimal = BigDecimal.ZERO,
    var totalPrice: BigDecimal = BigDecimal.ZERO,
)
