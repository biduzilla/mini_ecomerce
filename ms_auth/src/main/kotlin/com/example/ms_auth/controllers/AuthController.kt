package com.example.ms_auth.controllers

import com.example.ms_auth.dtos.AuthResponse
import com.example.ms_auth.dtos.LoginRequest
import com.example.ms_auth.dtos.RefreshTokenRequest
import com.example.ms_auth.services.IAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: IAuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.authenticate(loginRequest))
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken))
    }
}