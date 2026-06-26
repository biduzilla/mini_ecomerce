package com.example.kafka.event

import java.time.LocalDateTime
import java.util.UUID

data class AvailabilityCheckEvent(
    val eventId: UUID = UUID.randomUUID(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val orderId: UUID= UUID.randomUUID(),
    val available: Boolean=false,
    val details: List<ItemAvailabilityDetailEvent> = listOf()
)

data class ItemAvailabilityDetailEvent(
    val productId: UUID= UUID.randomUUID(),
    val requested: Long=0L,
    val available: Long=0L
)
