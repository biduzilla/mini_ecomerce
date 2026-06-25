package com.example.ms_stock.controllers

import com.example.ms_stock.dtos.CreateStockRequest
import com.example.ms_stock.dtos.ErrorDTO
import com.example.ms_stock.dtos.StockResponseDTO
import com.example.ms_stock.dtos.UpdateStockRequest
import com.example.ms_stock.dtos.toModel
import com.example.ms_stock.models.toResponseDTO
import com.example.ms_stock.services.IStockService
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
@RequestMapping("/api/stocks")
@Tag(name = "Stocks", description = "CRUD completo de estoque com integração com microsserviço de produtos")
class StockController(
    private val stockService: IStockService
) {
    @GetMapping
    @Operation(
        summary = "Listar estoques",
        description = "Retorna uma lista paginada de estoques com filtro opcional por ID do produto."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    )
    fun findAll(
        @Parameter(description = "Número da página (começa em 0)")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(defaultValue = "10") size: Int,

        @Parameter(description = "Termo de busca (filtra por ID do produto)")
        @RequestParam(required = false) search: String?
    ): ResponseEntity<Page<StockResponseDTO>> {
        val stockPage = stockService.findAll(page, size, search)
        val responsePage = stockPage.map { it.toResponseDTO() }
        return ResponseEntity.ok(responsePage)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estoque por ID")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Estoque encontrado",
            content = [Content(schema = Schema(implementation = StockResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Estoque não encontrado",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun findById(
        @Parameter(description = "ID único do estoque", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable id: UUID
    ): ResponseEntity<StockResponseDTO> {
        val stock = stockService.findById(id)
        return ResponseEntity.ok(stock.toResponseDTO())
    }

    @PostMapping
    @Operation(
        summary = "Criar novo estoque",
        description = "Cria um novo registro de estoque. Valida se o produto existe no microsserviço de produtos antes de persistir."
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Estoque criado com sucesso"),
        ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado no microsserviço de produtos",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Erro de validação nos campos",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun createStock(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do estoque a ser criado",
            required = true
        )
        request: CreateStockRequest
    ): ResponseEntity<StockResponseDTO> {
        val createdStock = stockService.createStock(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock.toResponseDTO())
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar estoque",
        description = "Atualiza a quantidade disponível de um estoque. Exige a versão atual do registro para controle de concorrência otimista (Optimistic Locking)."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
        ApiResponse(
            responseCode = "404",
            description = "Estoque não encontrado",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        ),
        ApiResponse(
            responseCode = "409",
            description = "Conflito de versão - outro usuário atualizou o registro",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun updateStock(
        @Parameter(description = "ID do estoque a ser atualizado", required = true)
        @PathVariable id: UUID,

        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados do estoque, incluindo a versão atual",
            required = true
        )
        request: UpdateStockRequest
    ): ResponseEntity<StockResponseDTO> {
        val stockToUpdate = request.toModel(id)
        val updatedStock = stockService.save(stockToUpdate)
        return ResponseEntity.ok(updatedStock.toResponseDTO())
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar estoque")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Estoque deletado com sucesso (sem corpo)"),
        ApiResponse(
            responseCode = "404",
            description = "Estoque não encontrado",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun deleteById(
        @Parameter(description = "ID do estoque a ser deletado", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        stockService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}