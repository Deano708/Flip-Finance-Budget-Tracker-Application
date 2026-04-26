package com.example.flipfinance.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

/*
   Title: Use Supabase with Android Kotlin
   Author: Supabase
   Date: 26 April 2026
   Date accessed: 26/04/2026
   Availability: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
*/

val supabase = createSupabaseClient(
    supabaseUrl = "https://pwfnhcztrnnoxpuhbxdb.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB3Zm5oY3p0cm5ub3hwdWhieGRiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzcxNjA4MDAsImV4cCI6MjA5MjczNjgwMH0.RVhGoK5CnhictgH5umRdFlrIcJ90JACvkJF-0AJBncw"
) {
    install(Storage)
}