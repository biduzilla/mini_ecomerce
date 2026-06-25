package com.example.ms_order.models

import com.example.ms_order.enums.OrderStatusEnum
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "ORDERS")
@SQLRestriction("deleted <> true")
@SQLDelete(sql = "UPDATE ORDERS SET deleted = true, updated_at = NOW() WHERE id = ?")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatusEnum = OrderStatusEnum.AWAITING_STOCK,
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var items: MutableList<OrderItem> = mutableListOf(),

    @Column(name = "cancellation_reason")
    var cancellationReason: String? = null
) : BaseModel() {
    fun calculateTotal(): BigDecimal {
        return items.sumOf { it.totalPrice }
    }
}

