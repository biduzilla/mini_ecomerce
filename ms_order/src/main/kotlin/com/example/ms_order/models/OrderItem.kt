package com.example.ms_order.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "ORDER_ITEM")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "product_id")
    var productId: UUID? = null,
    var quantity: Int = 1,
    var unitPrice: BigDecimal = BigDecimal.ZERO,
    var totalPrice: BigDecimal = BigDecimal.ZERO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null
): BaseModel()
