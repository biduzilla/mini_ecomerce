package com.example.ms_order.consumers

import com.example.kafka.event.AvailabilityCheckEvent
import com.example.ms_order.enums.OrderStatusEnum
import com.example.ms_order.services.IOrderService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class StockEventConsumer(
    private val orderService: IOrderService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(StockEventConsumer::class.java)
    }

    @KafkaListener(
        topics = ["stock.check-result"],
        groupId = "\${spring.kafka.consumer.group-id:order-service-group}"
    )
    fun handleAvailabilityCheck(event: AvailabilityCheckEvent) {
        logger.info("📩 Recebido stock.check-result: ${event.eventId} | Pedido: ${event.orderId}")

        try {
            val status = if (event.available) {
                logger.info("Estoque suficiente para pedido ${event.orderId}")
                OrderStatusEnum.APPROVED
            } else {
                logger.warn("Estoque insuficiente para pedido ${event.orderId}")
                OrderStatusEnum.REJECTED
            }

            orderService.updateOrderStatus(event.orderId, status)

            logger.info("✅ Pedido ${event.orderId} atualizado para status: $status")

        } catch (e: Exception) {
            logger.error("Erro ao atualizar status do pedido ${event.orderId}", e)
            throw e
        }
    }
}