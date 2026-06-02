package com.example.flipfinance.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flipfinance.MainActivity
import com.example.flipfinance.Preferences.Settings.SettingsRepository
import com.example.flipfinance.data.local.util.FirebaseTransactionSource
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: BudgetNotificationWorker.
• AI Tool: Claude Sonnet 4.6
• Purpose/intention : Design and syntax implementation of BudgetNotification worker.
• Date(s) 01/06/2026.
• https://claude.ai/share/943aa681-7632-451c-84c2-b814e218caae
*/

// Channel IDs — one channel per notification type
const val CHANNEL_OVER_BUDGET   = "channel_over_budget"
const val CHANNEL_MONTHLY_CONGRATS = "channel_monthly_congrats"

// Notification IDs
const val NOTIF_ID_OVER_BUDGET   = 1001
const val NOTIF_ID_CONGRATS      = 1002

@HiltWorker
class BudgetNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val firebaseSource: FirebaseTransactionSource
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.success() // Not logged in — nothing to check

        // Read the latest settings from DataStore
        val settings = settingsRepository.settingsFlow.firstOrNull()
            ?: return Result.success()

        // Notifications toggled off in settings — do nothing
        if (!settings.budgetAlertsEnabled) return Result.success()

        val maxBudget = settings.maxBudget.toDoubleOrNull() ?: 0.0
        if (maxBudget <= 0.0) return Result.success() // No budget configured

        // Read all transactions and filter to current month's expenses
        val allTransactions = firebaseSource.getTransactionsByUser(currentUserId).firstOrNull()
            ?: return Result.success()

        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear  = now.get(Calendar.YEAR)

        val monthlyExpenses = allTransactions
            .filter { tx ->
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.date }
                txCal.get(Calendar.MONTH) == currentMonth &&
                        txCal.get(Calendar.YEAR)  == currentYear  &&
                        tx.expenseType.equals("Expense", ignoreCase = true)
            }
            .sumOf { it.amount }

        ensureNotificationChannelsExist()

        when {
            // Over budget — fire warning every time this worker runs while over limit
            monthlyExpenses > maxBudget -> {
                val overage = monthlyExpenses - maxBudget
                showOverBudgetNotification(
                    currencySymbol = settings.currency.symbol,
                    overage = overage,
                    maxBudget = maxBudget
                )
            }

            // Under budget and it is the last day of the month — send congratulations once
            isLastDayOfMonth(now) && monthlyExpenses <= maxBudget -> {
                showCongratulatoryNotification(
                    currencySymbol = settings.currency.symbol,
                    saved = maxBudget - monthlyExpenses,
                    maxBudget = maxBudget
                )
            }
        }

        return Result.success()
    }

    // ── Notification builders ─────────────────────────────────────────────────

    private fun showOverBudgetNotification(
        currencySymbol: String,
        overage: Double,
        maxBudget: Double
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_OVER_BUDGET)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Budget Limit Exceeded")
            .setContentText(
                "You're ${currencySymbol}${String.format("%.2f", overage)} over your " +
                        "${currencySymbol}${String.format("%.2f", maxBudget)} monthly limit."
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Your monthly expenses have exceeded your maximum budget of " +
                            "${currencySymbol}${String.format("%.2f", maxBudget)}. " +
                            "You are currently ${currencySymbol}${String.format("%.2f", overage)} over. " +
                            "Open FlipFinance to review your spending."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        getNotificationManager().notify(NOTIF_ID_OVER_BUDGET, notification)
    }

    private fun showCongratulatoryNotification(
        currencySymbol: String,
        saved: Double,
        maxBudget: Double
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MONTHLY_CONGRATS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Great Work This Month!")
            .setContentText(
                "You stayed under your ${currencySymbol}${String.format("%.2f", maxBudget)} budget!"
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Congratulations! You finished the month under your budget of " +
                            "${currencySymbol}${String.format("%.2f", maxBudget)} and saved " +
                            "${currencySymbol}${String.format("%.2f", saved)}. " +
                            "Keep up the excellent financial habits!"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        getNotificationManager().notify(NOTIF_ID_CONGRATS, notification)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun ensureNotificationChannelsExist() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getNotificationManager()

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_OVER_BUDGET,
                    "Budget Warnings",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when your monthly expenses exceed your set maximum budget."
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_MONTHLY_CONGRATS,
                    "Monthly Achievements",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Monthly congratulations when you stay under your budget."
                }
            )
        }
    }

    private fun getNotificationManager(): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun isLastDayOfMonth(cal: Calendar): Boolean {
        return cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}