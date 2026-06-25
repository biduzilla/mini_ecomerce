package com.example.ms_stock.repositories

import com.example.ms_stock.models.Stock
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StockRepository: JpaRepository<Stock, UUID> {
    @Query(
        """
        select 
            s
        from Stock s
        where :search is null or s.productId like %:search%
    """
    )
    fun search(@Param("search") search: String?, pageable: Pageable): Page<Stock>

    @Query(
        """
        select s
        from Stock s
        where s.id = :stockId
    """
    )
    fun findByID(@Param("stockId") stockId: UUID): Stock?

    @Query(
        """
        select s
        from Stock s
        where s.productId = :productId
    """
    )
    fun findByProductId(@Param("productId") productId: UUID):Stock?

    @Query("SELECT s FROM Stock s WHERE s.productId IN :ids")
    fun findAllByProductIdIn(@Param("ids") ids: List<UUID>): List<Stock>
}