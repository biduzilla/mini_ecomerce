package com.example.ms_stock.consumers

import com.example.kafka.event.AvailabilityCheckEvent
import com.example.kafka.event.ItemAvailabilityDetailEvent
import com.example.kafka.event.OrderCreatedEvent
import com.example.ms_stock.producers.StockEventProducer
import com.example.ms_stock.services.IStockService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer(
    private val stockService: IStockService,
    private val stockEventProducer: StockEventProducer
) {
    companion object {
        private val logger = LoggerFactory.getLogger(OrderEventConsumer::class.java)
    }

    @KafkaListener(
        topics = ["orders"],
        groupId = "\${spring.kafka.consumer.group-id:stock-service-group}"
    )
    fun handleOrderCreated(event: OrderCreatedEvent) {
        logger.info("📩 Recebido OrderCreatedEvent: ${event.eventId} | Pedido: ${event.orderId}")

        try {
            val checkRequest = com.example.ms_stock.dtos.AvailabilityCheckRequest(
                items = event.items.map { item ->
                    com.example.ms_stock.dtos.ItemRequest(
                        productId = item.productId!!,
                        quantity = item.quantity.toLong()
                    )
                }
            )

            val checkResponse = stockService.checkAvailability(checkRequest)

            val availabilityEvent = AvailabilityCheckEvent(
                orderId = event.orderId,
                available = checkResponse.available,
                details = checkResponse.details.map { detail ->
                    ItemAvailabilityDetailEvent(
                        productId = detail.productId,
                        requested = detail.requested,
                        available = detail.available
                    )
                }
            )

            stockEventProducer.publishAvailabilityCheck(availabilityEvent)

            logger.info(
                "✅ Verificação de estoque para pedido ${event.orderId}: " +
                        "disponível=${checkResponse.available}"
            )

        } catch (e: Exception) {
            logger.error("Erro ao processar pedido ${event.orderId}", e)
            throw e
        }
    }
}