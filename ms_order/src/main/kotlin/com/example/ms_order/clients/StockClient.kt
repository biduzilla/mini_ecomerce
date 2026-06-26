package com.example.ms_order.clients

import com.example.ms_order.dtos.AvailabilityCheckRequest
import com.example.ms_order.dtos.AvailabilityCheckResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "stock-service",
    url = $$"${stock.service.url}"
)
interface StockClient {
    @PostMapping("/check-availability")
    fun checkAvailability(
        @RequestBody request: AvailabilityCheckRequest
    ): AvailabilityCheckResponse
}