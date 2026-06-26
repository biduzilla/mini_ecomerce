package com.example.ms_order.configs

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun stockCheckResultTopic(): NewTopic {
        return TopicBuilder.name("stock.check-result")
            .partitions(3)
            .replicas(1)
            .build()
    }
}