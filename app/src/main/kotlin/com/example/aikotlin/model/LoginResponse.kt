package com.example.aikotlin.model

data class LoginResponse(
    val token: String,
    val userId: String,
    val username: String
)
