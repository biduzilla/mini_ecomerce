package com.example.ms_order.dtos

import java.util.*

data class AvailabilityCheckRequest(
    val items: List<ItemRequest>
)

data class ItemRequest(
    val productId: UUID,
    val quantity: Long
)

data class AvailabilityCheckResponse(
    val available: Boolean,
    val details: List<ItemAvailabilityDetail>
)

data class ItemAvailabilityDetail(
    val productId: UUID,
    val requested: Long,
    val available: Long
)