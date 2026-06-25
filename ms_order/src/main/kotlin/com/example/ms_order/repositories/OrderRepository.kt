package com.example.ms_order.repositories

import com.example.ms_order.models.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository: JpaRepository<Order, UUID> {
}