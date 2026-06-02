package com.example.flipfinance.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipfinance.domain.model.Badge
import com.example.flipfinance.domain.repository.BadgeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/*
   Title: State - Android Jetpack Compose - Part 6
   Author: Phillip Lackner
   Date: 5 years ago
   Date accessed: 01/06/2026
   Code version: 1
   Availability: https://youtu.be/s3m1PSd7VWc?si=W9D10o-CFGRSg9Ex
*/
/*
Title: Disclosure of AI Usage in my Assessment.
• Section: BadgeViewModel.
• AI Tool: Gemini
• Purpose/intention :Syntax implementation in the viewmodel
• Date(s) 02/06/2026.
• https://gemini.google.com/share/b4ac44f6b10b
*/

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repository: BadgeRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val uid = firebaseAuth.currentUser?.uid ?: ""

    // shows the full list of badges — locked and unlocked ones reactively
    val badges: StateFlow<List<Badge>> = repository.getBadges(uid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Map directly from the badges state flow to count unlocked items
    val unlockedCount: StateFlow<Int> = badges
        .map { list -> list.count { it.isUnlocked } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}