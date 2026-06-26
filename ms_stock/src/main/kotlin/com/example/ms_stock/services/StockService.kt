package com.example.ms_stock.services

import com.example.ms_stock.clients.ProductClient
import com.example.ms_stock.dtos.AvailabilityCheckRequest
import com.example.ms_stock.dtos.AvailabilityCheckResponse
import com.example.ms_stock.dtos.CreateStockRequest
import com.example.ms_stock.dtos.ItemAvailabilityDetail
import com.example.ms_stock.exceptions.NotFoundException
import com.example.ms_stock.models.Stock
import com.example.ms_stock.repositories.StockRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

interface IStockService {
    fun findAll(page: Int = 0, size: Int = 10, search: String?): Page<Stock>
    fun findById(id: UUID): Stock
    fun createStock(request: CreateStockRequest): Stock
    fun save(model: Stock): Stock
    fun deleteById(id: UUID)
    fun findByProductId(productId: UUID): Stock
    fun checkAvailability(request: AvailabilityCheckRequest): AvailabilityCheckResponse
}

@Service
class StockService(
    private val stockRepo: StockRepository,
    private val productClient: ProductClient
) : IStockService {
    override fun findAll(
        page: Int,
        size: Int,
        search: String?
    ): Page<Stock> {
        val pageable = PageRequest.of(page, size)
        return stockRepo.search(UUID.fromString(search), pageable)
    }

    override fun findById(id: UUID): Stock {
        return stockRepo.findByID(id)
            ?: throw NotFoundException("Stock not found")
    }

    override fun createStock(request: CreateStockRequest): Stock {
        val productExists = productClient.findById(request.productId)
        val stockToSave = Stock(
            productId = productExists.id,
            availableQuantity = request.availableQuantity
        )

        return save(stockToSave)
    }

    @Transactional
    override fun save(model: Stock): Stock {
        return stockRepo.save(model)
    }

    @Transactional
    override fun deleteById(id: UUID) {
        stockRepo.deleteById(id)
    }

    override fun findByProductId(productId: UUID): Stock {
        return stockRepo.findByID(productId)
            ?: throw NotFoundException("Stock not found")
    }

    override fun checkAvailability(request: AvailabilityCheckRequest): AvailabilityCheckResponse {
        val productsIds = request.items.map { it.productId }
        val stocks = stockRepo.findAllByProductIdIn(productsIds).associateBy { it.productId!! }
        val details = request.items.mapNotNull { item ->
            val available = stocks[item.productId]?.availableQuantity ?: 0
            if (item.quantity > available) {
                ItemAvailabilityDetail(item.productId, item.quantity, available)
            } else null
        }

        return if (details.isEmpty()) {
            AvailabilityCheckResponse(true, emptyList())
        } else {
            AvailabilityCheckResponse(false, details)
        }
    }
}