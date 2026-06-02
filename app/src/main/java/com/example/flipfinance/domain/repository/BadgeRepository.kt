package com.example.flipfinance.domain.repository

import com.example.flipfinance.domain.model.Badge
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getBadges(uid: String): Flow<List<Badge>>
}