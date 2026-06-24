package com.example.ms_auth.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import com.example.ms_auth.models.User
import java.util.*

data class RegisterUserDTO(
    var id: UUID? = null,
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @field:Pattern(
        regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$",
        message = "Name must contain only letters and spaces"
    )
    var name: String = "",
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = 100, message = "Email must be at most 100 characters")
    var email: String = "",
    @field:NotBlank(message = "Password is required")
    var password: String = "",
)

fun RegisterUserDTO.toModel(): User = User(
    id = id,
    name = name,
    email = email,
    keyword = password
)
