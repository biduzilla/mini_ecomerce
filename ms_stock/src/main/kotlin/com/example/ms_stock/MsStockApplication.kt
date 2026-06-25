package com.example.ms_stock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
class MsStockApplication

fun main(args: Array<String>) {
	runApplication<MsStockApplication>(*args)
}
