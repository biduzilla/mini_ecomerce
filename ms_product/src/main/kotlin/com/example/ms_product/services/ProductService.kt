package com.example.ms_product.services

import com.br.ms_product.exceptions.NotFoundException
import com.example.ms_product.models.Product
import com.example.ms_product.repositories.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

interface IProductService {
    fun findAll(page: Int = 0, size: Int = 10, search: String?): Page<Product>
    fun findById(id: UUID): Product
    fun save(product: Product): Product
    fun deleteById(id: UUID)
}

@Service
class ProductService(
    private val productRepository: ProductRepository,
) : IProductService {
    override fun findAll(
        page: Int,
        size: Int,
        search: String?
    ): Page<Product> {
        val pageable = PageRequest.of(page, size)
        return productRepository.search(search, pageable)
    }

    override fun findById(id: UUID): Product {
        return productRepository.findByID(id)
            ?: throw NotFoundException("Product not found")
    }

    @Transactional
    override fun save(product: Product): Product {
        return productRepository.save(product)
    }

    @Transactional
    override fun deleteById(id: UUID) {
        productRepository.deleteById(id)
    }
}