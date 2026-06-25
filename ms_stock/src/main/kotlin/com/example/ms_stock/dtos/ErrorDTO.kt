package com.example.ms_stock.dtos

import java.time.LocalDateTime

data class ErrorDTO(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String
)