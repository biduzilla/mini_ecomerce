package com.example.ms_auth.controllers

import com.example.ms_auth.dtos.RegisterUserDTO
import com.example.ms_auth.dtos.UserDTO
import com.example.ms_auth.dtos.toModel
import com.example.ms_auth.models.toDTO
import com.example.ms_auth.services.IUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: IUserService) {
    @PostMapping
    fun register(@Valid @RequestBody user: RegisterUserDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.save(user.toModel()).toDTO())
    }
}