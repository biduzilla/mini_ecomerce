package com.example.ms_stock.configs

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun ordersTopic(): NewTopic {
        return TopicBuilder.name("orders")
            .partitions(3)
            .replicas(1)
            .build()
    }
}