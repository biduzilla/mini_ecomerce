package com.example.ms_order.repositories

import com.example.ms_order.models.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderItemRepository : JpaRepository<OrderItem, UUID> {
}