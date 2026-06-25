package com.example.ms_stock.models

import com.example.ms_stock.dtos.StockResponseDTO
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.*

@Entity
@Table(name = "STOCK")
@SQLRestriction("deleted <> true")
@SQLDelete(sql = "UPDATE STOCK SET deleted = true, updated_at = NOW() WHERE id = ?")
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var productId: UUID? = null,
    var availableQuantity: Long = 0,
    @Version
    var version: Int = 0
) : BaseModel()

fun Stock.toResponseDTO(): StockResponseDTO {
    return StockResponseDTO(
        id = this.id!!,
        productId = this.productId!!,
        availableQuantity = this.availableQuantity,
        version = this.version
    )
}