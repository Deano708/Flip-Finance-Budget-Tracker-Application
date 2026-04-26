package com.example.flipfinance.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

val supabase = createSupabaseClient(
    supabaseUrl = "https://supabase.co",
    supabaseKey = "your-anon-key"
) {
    install(Storage)
}