package com.example.ms_product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
class MsProductApplication

fun main(args: Array<String>) {
	runApplication<MsProductApplication>(*args)
}
