package com.example.flipfinance.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val profileImageUrl: String? = null
)