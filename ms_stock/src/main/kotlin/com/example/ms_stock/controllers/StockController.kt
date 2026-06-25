package com.example.ms_stock.controllers

import com.example.ms_stock.dtos.CreateStockRequest
import com.example.ms_stock.dtos.StockResponseDTO
import com.example.ms_stock.dtos.UpdateStockRequest
import com.example.ms_stock.models.toResponseDTO
import com.example.ms_stock.services.IStockService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/stocks")
class StockController(
    private val stockService: IStockService
) {

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<Page<StockResponseDTO>> {
        val stockPage = stockService.findAll(page, size, search)
        val responsePage = stockPage.map { it.toResponseDTO() }

        return ResponseEntity.ok(responsePage)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<StockResponseDTO> {
        val stock = stockService.findById(id)
        return ResponseEntity.ok(stock.toResponseDTO())
    }

    @PostMapping
    fun createStock(@Valid @RequestBody request: CreateStockRequest): ResponseEntity<StockResponseDTO> {
        val createdStock = stockService.createStock(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock.toResponseDTO())
    }

    @PutMapping("/{id}")
    fun updateStock(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateStockRequest
    ): ResponseEntity<StockResponseDTO> {
        val existingStock = stockService.findById(id)

        existingStock.availableQuantity = request.availableQuantity

        val updatedStock = stockService.save(existingStock)
        return ResponseEntity.ok(updatedStock.toResponseDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: UUID): ResponseEntity<Void> {
        stockService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}