package com.example.ms_product.controllers

import com.example.ms_product.dtos.ErrorDTO
import com.example.ms_product.dtos.ProductDTO
import com.example.ms_product.dtos.toModel
import com.example.ms_product.models.toDTO
import com.example.ms_product.services.IProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "CRUD completo de produtos")
class ProductControllers(
    private val productService: IProductService
) {
    @GetMapping
    @Operation(
        summary = "Listar produtos",
        description = "Retorna uma lista paginada de produtos com filtro opcional por nome."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    )
    fun findAll(
        @Parameter(description = "Termo de busca (filtra por nome)")
        @RequestParam(required = false) search: String?,

        @Parameter(description = "Número da página (começa em 0)")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(defaultValue = "10") size: Int
    ): Page<ProductDTO> {
        return productService.findAll(page, size, search).map { p -> p.toDTO() }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Produto encontrado",
            content = [Content(schema = Schema(implementation = ProductDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun findById(
        @Parameter(
            description = "ID único do produto",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable id: UUID
    ): ResponseEntity<ProductDTO> {
        return ResponseEntity.ok(productService.findById(id).toDTO())
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo produto")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        ApiResponse(
            responseCode = "422",
            description = "Erro de validação nos campos",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun create(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do produto a ser criado",
            required = true
        )
        product: ProductDTO
    ): ProductDTO {
        return productService.save(product.toModel()).toDTO()
    }

    @PutMapping
    @ResponseStatus
    @Operation(summary = "Atualizar produto existente")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
    )
    fun update(@Valid @RequestBody product: ProductDTO): ProductDTO {
        return productService.save(product.toModel()).toDTO()
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar produto")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Produto deletado com sucesso (sem corpo)"),
        ApiResponse(responseCode = "404", description = "Produto não encontrado")
    )
    fun deleteById(
        @Parameter(description = "ID do produto a ser deletado", required = true)
        @PathVariable id: UUID
    ) {
        productService.deleteById(id)
    }
}