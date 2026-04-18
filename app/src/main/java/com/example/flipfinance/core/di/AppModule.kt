package com.example.flipfinance.core.di

import com.example.flipfinance.data.remote.FirebaseAuthRepository
import com.example.flipfinance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
   Title: Adding in simple Firebase Authentication
   Author: ebAdamZA
   Date: 13 March 2026
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://github.com/PROG7313-2026-EMDBN/Sandbox.git
*/

/*
   Title: All about Firebase Authentication 🔥 | Login & Signup | Jetpack Compose
   Author: Easy Tuto
   Date: 1 year ago
   Date accessed: 18/04/2026
   Code version : 1
   Availability: https://youtu.be/KOnLpNZ4AFc?si=0_ykHIWXJTBMgkbb
*/

// Wiring up the Firebase instance and the Repository
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}