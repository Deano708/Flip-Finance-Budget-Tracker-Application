package com.example.flipfinance.supabase
import com.example.flipfinance.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

/*
   Title: Supabase Avatar Storage Service
   Author: ebadamZA
   Date: 13 March 2026
   Date accessed: 29/04/2026
   Code version: 1
   Availability: https://github.com/PROG7313-2026-EMDBN/Sandbox.git
*/

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Storage)
    }
}
