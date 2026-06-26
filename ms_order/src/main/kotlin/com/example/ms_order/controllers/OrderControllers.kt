package com.example.ms_order.controllers

import com.example.ms_order.dtos.*
import com.example.ms_order.services.IOrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Gerenciamento de pedidos com validação de estoque e publicação de eventos")
class OrderControllers(
    private val orderService: IOrderService
) {

    @PostMapping
    @Operation(
        summary = "Criar pedido",
        description = "Cria um novo pedido, valida a disponibilidade de estoque no microsserviço de produtos, persiste no banco e publica evento Kafka."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "Pedido criado com sucesso"
        ),
        ApiResponse(
            responseCode = "400",
            description = "Erro de validação ou estoque indisponível",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        ),
        ApiResponse(
            responseCode = "422",
            description = "Erro de validação nos campos",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun createOrder(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do pedido a ser criado",
            required = true
        )
        request: CreateOrderRequest
    ): ResponseEntity<OrderResponseDTO> {
        val order = request.toModel()
        val createdOrder = orderService.processOrder(order)

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder.toResponseDTO())
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado",
            content = [Content(schema = Schema(implementation = OrderResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Pedido não encontrado",
            content = [Content(schema = Schema(implementation = ErrorDTO::class))]
        )
    )
    fun findById(
        @Parameter(
            description = "ID único do pedido",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable id: UUID
    ): OrderResponseDTO {
        return orderService.findById(id).toResponseDTO()
    }

    @GetMapping
    @Operation(
        summary = "Listar pedidos",
        description = "Retorna uma lista paginada de pedidos do usuário autenticado"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    )
    fun findAll(
        @Parameter(description = "Número da página (começa em 0)")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(defaultValue = "10") size: Int
    ): List<OrderResponseDTO> {
        return orderService.findByAll().map { it.toResponseDTO() }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar pedido")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
    )
    fun cancelOrder(
        @Parameter(description = "ID do pedido a ser cancelado", required = true)
        @PathVariable id: UUID
    ) {
        orderService.deleteById(id)
    }
}