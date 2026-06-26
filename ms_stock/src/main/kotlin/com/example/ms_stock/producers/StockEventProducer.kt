package com.example.ms_stock.producers

import com.example.kafka.event.AvailabilityCheckEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class StockEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, AvailabilityCheckEvent>
) {
    companion object {
        private const val TOPIC = "stock.check-result"
        private val logger = LoggerFactory.getLogger(StockEventProducer::class.java)
    }

    fun publishAvailabilityCheck(event: AvailabilityCheckEvent) {
        kafkaTemplate.send(
            TOPIC,
            event.orderId.toString(),
            event
        ).whenComplete { _, ex ->
            if (ex == null) {
                logger.info(
                    "Evento stock.check-result publicado para pedido ${event.orderId}: " +
                            "available=${event.available}"
                )
            } else {
                logger.error(
                    "Falha ao publicar evento de verificação de estoque: ${event.orderId}",
                    ex
                )
            }
        }
    }
}