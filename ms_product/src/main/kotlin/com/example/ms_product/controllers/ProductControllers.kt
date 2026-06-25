package com.example.ms_product.controllers

import com.example.ms_product.dtos.ProductDTO
import com.example.ms_product.dtos.toModel
import com.example.ms_product.models.toDTO
import com.example.ms_product.services.IProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/products")
class ProductControllers(
    private val productService: IProductService
) {
    @GetMapping
    fun findAll(
        @RequestParam(required = false) search: String?,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10
    ): Page<ProductDTO> {
        return productService.findAll(page, size, search).map { p -> p.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: UUID
    ): ProductDTO {
        return productService.findById(id).toDTO()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody product: ProductDTO): ProductDTO {
        return productService.save(product.toModel()).toDTO()
    }

    @PutMapping
    @ResponseStatus
    fun update(@Valid @RequestBody product: ProductDTO): ProductDTO {
        return productService.save(product.toModel()).toDTO()
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(
        @PathVariable id: UUID
    ) {
        productService.deleteById(id)
    }
}