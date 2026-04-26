package com.example.flipfinance.ui.components.transactions

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log

/*
   Title: Use Supabase with Android Kotlin
   Author: Supabase
   Date: 26 April 2026
   Date accessed: 26/04/2026
   Availability: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
*/

/*
   Title: Storage | Supabase | Jetpack Compose | Tutorial | 2023
   Author: YoursSohail
   Date: 23 October 2023
   Date accessed: 26/04/2026
   Availability: https://www.youtube.com/watch?v=BqxI7ViS_-M
*/



fun downloadReceipt(context: Context, url: String, title: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Receipt: $title")
            .setDescription("Flip Finance Receipt")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "receipt_${System.currentTimeMillis()}.jpg")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    } catch (e: Exception) {
        Log.e("DownloadError", "Failed to start download: ${e.message}")
    }
}