package com.example.ms_auth.enums

enum class RoleEnum {
    ADMIN, CLIENT;

    val authority: String
        get() = "ROLE_${name}"
}