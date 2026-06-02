package com.example.flipfinance.workers

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: NotificationScheduler.
• AI Tool: Claude Sonnet 4.6
• Purpose/intention : Design and syntax implementation of Notification scheduler, allowing for a toggle of notification, an immediate notification once an expense has been made and a 6 hourly trigger if over budget.
• Date(s) 01/06/2026.
• https://claude.ai/share/943aa681-7632-451c-84c2-b814e218caae
*/

object NotificationScheduler {

    private const val WORK_NAME = "budget_notification_check"
    private const val IMMEDIATE_WORK_NAME = "immediate_budget_check"

    // Call this once from MainActivity after the user is logged in.
    // WorkManager deduplicates by WORK_NAME so calling it multiple times is safe —
    // it will only schedule one recurring job.
    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Needs Firebase access
            .build()

        // Runs every 6 hours — frequent enough to catch over-budget events soon
        // after a transaction is added, without draining the battery.
        val workRequest = PeriodicWorkRequestBuilder<BudgetNotificationWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            // KEEP means: if a job with this name already exists, don't replace it.
            // This prevents re-scheduling on every app open while still ensuring
            // the job exists on first launch.
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun runImmediateCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<BudgetNotificationWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Replaces any currently running instant check
            oneTimeRequest
        )
    }

    // Call this from SettingsViewModel when budget alerts are toggled OFF
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}