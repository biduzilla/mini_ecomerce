package com.example.ms_stock.clients

import com.example.ms_stock.dtos.ProductDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(
    name = "product-service",
    url = $$"${product.service.url}"
)
interface ProductClient {
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: UUID
    ): ProductDTO
}