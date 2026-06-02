package com.example.flipfinance.core.di

import android.content.Context
import com.example.flipfinance.data.remote.FirebaseAuthRepository
import com.example.flipfinance.data.remote.FirebaseBadgeRepository
import com.example.flipfinance.data.remote.FirebaseProfileRepository
import com.example.flipfinance.domain.repository.AuthRepository
import com.example.flipfinance.domain.repository.BadgeRepository
import com.example.flipfinance.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration


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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: HiltWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance("https://flipfinance-cba5d-default-rtdb.europe-west1.firebasedatabase.app/")


    @Provides
    @Singleton
    fun provideAuthRepository(
        fbAuth: FirebaseAuth,
        fbDatabase: FirebaseDatabase
    ): AuthRepository = FirebaseAuthRepository(fbAuth, fbDatabase)

    @Provides
    @Singleton
    fun provideProfileRepository(
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: FirebaseDatabase,
        @ApplicationContext context: Context
    ): ProfileRepository = FirebaseProfileRepository(firebaseAuth, firebaseDatabase, context)

    @Provides
    @Singleton
    fun provideBadgeRepository(
        firebaseDatabase: FirebaseDatabase
    ): BadgeRepository {
        return FirebaseBadgeRepository(firebaseDatabase)
    }
}