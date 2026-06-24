package com.example.ms_product.repositories

import com.example.ms_product.models.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ProductRepository : JpaRepository<Product, UUID> {
    @Query(
        """
        select 
            p 
        from Product p
        where :search is null or p.name like %:search%
    """
    )
    fun search(@Param("search") search: String?, pageable: Pageable): Page<Product>

    @Query(
        """
        select p
        from Product p
        where p.id = :productId
    """
    )
    fun findByID(@Param("productId") productId: UUID): Product?
}