package com.example.ms_order.services

import com.example.kafka.event.OrderCreatedEvent
import com.example.kafka.event.OrderItemEvent
import com.example.ms_order.clients.StockClient
import com.example.ms_order.dtos.AvailabilityCheckRequest
import com.example.ms_order.dtos.ItemRequest
import com.example.ms_order.enums.OrderStatusEnum
import com.example.ms_order.exceptions.BadRequestException
import com.example.ms_order.exceptions.NotFoundException
import com.example.ms_order.models.Order
import com.example.ms_order.producers.OrderEventProducers
import com.example.ms_order.repositories.OrderRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

interface IOrderService {
    fun save(order: Order): Order
    fun processOrder(order: Order): Order
    fun findById(id: UUID): Order
    fun findByAll(): List<Order>
    fun deleteById(id: UUID)
    fun updateOrderStatus(id: UUID, status: OrderStatusEnum)
}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val stockClient: StockClient,
    private val eventProducers: OrderEventProducers
) : IOrderService {

    private fun checkAvailability(order: Order) {
        val invalidItems = order.items.filter { it.productId == null || it.quantity <= 0 }
        if (invalidItems.isNotEmpty()) {
            throw BadRequestException("Itens inválidos no pedido: ${invalidItems.map { it.productId ?: "nulo" }}")
        }

        val availabilityRequest = order.items.map { item ->
            ItemRequest(item.productId!!, item.quantity.toLong())
        }.let { AvailabilityCheckRequest(it) }

        val response = stockClient.checkAvailability(availabilityRequest)

        if (!response.available) {
            val message = response.details.joinToString("; ") {
                "produto ${it.productId}: pedido ${it.requested}, disponível ${it.available}"
            }
            throw BadRequestException("The available quantity is invalid: $message")
        }
    }

    @Transactional
    override fun save(order: Order): Order {
        return orderRepository.save(order.apply {
            totalAmount = order.calculateTotal()
        })
    }

    @Transactional
    override fun processOrder(order: Order): Order {
        checkAvailability(order)
        val order = save(order)

        eventProducers.publishOrderCreated(
            OrderCreatedEvent(
                orderId = order.id!!,
                items = order.items.map { item ->
                    OrderItemEvent(
                        id = item.id!!,
                        productId = item.productId!!,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        totalPrice = item.totalPrice
                    )
                },
                totalAmount = order.totalAmount,
                status = order.status
            )
        )
        return order
    }

    override fun findById(id: UUID): Order {
        return orderRepository.findById(id).orElseThrow {
            NotFoundException("Record not found $id")
        }
    }

    override fun findByAll(): List<Order> {
        return orderRepository.findAll()
    }

    override fun deleteById(id: UUID) {
        orderRepository.deleteById(id)
    }

    override fun updateOrderStatus(id: UUID, status: OrderStatusEnum) {
        val order = findById(id)
        order.status = status
        save(order)
    }


}