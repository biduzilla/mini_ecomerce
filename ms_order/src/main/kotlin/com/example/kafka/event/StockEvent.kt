package com.example.kafka.event

import java.time.LocalDateTime
import java.util.UUID

data class AvailabilityCheckEvent(
    val eventId: UUID = UUID.randomUUID(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val orderId: UUID,
    val available: Boolean,
    val details: List<ItemAvailabilityDetailEvent>
)

data class ItemAvailabilityDetailEvent(
    val productId: UUID,
    val requested: Long,
    val available: Long
)
