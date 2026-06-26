package com.example.ms_order.producers

import com.example.kafka.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderEventProducers(
    private val kafkaTemplate: KafkaTemplate<String, OrderCreatedEvent>
) {
    companion object {
        private const val TOPIC = "orders"
        private val logger = LoggerFactory.getLogger(OrderEventProducers::class.java)
    }

    fun publishOrderCreated(event: OrderCreatedEvent) {
        kafkaTemplate.send(
            TOPIC,
            event.orderId.toString(),
            event,
        ).whenComplete { _, ex ->
            if (ex == null) {
                logger.info("Evento pedido.criado publicado: ${event.orderId}")
            } else {
                logger.error(
                    "Falha ao publicar evento: ${event.eventId}",
                    ex
                )
            }
        }
    }
}