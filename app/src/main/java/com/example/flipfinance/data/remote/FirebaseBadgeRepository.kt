package com.example.flipfinance.data.remote

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.flipfinance.domain.model.Badge
import com.example.flipfinance.domain.repository.BadgeRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import javax.inject.Inject
/*
Title: Disclosure of AI Usage in my Assessment.
- Section: FirebaseBadgeRepository.
- AI Tool: Gemini
- Purpose/intention : Design and syntax implementation of badges
- Date(s) 02/06/2026.
- https://gemini.google.com/share/b4ac44f6b10b
*/
class FirebaseBadgeRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : BadgeRepository {

    override fun getBadges(uid: String): Flow<List<Badge>> = callbackFlow {
        val ref = firebaseDatabase.reference.child("transactions").child(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Parse all transactions from Firebase
                val transactions = snapshot.children.mapNotNull { child ->
                    try {
                        val title       = child.child("title").getValue(String::class.java) ?: ""
                        val amount      = child.child("amount").getValue(Double::class.java) ?: 0.0
                        val date        = child.child("date").getValue(Long::class.java) ?: 0L
                        val expenseType = child.child("expenseType").getValue(String::class.java) ?: ""
                        val receiptUrl  = child.child("receiptUrl").getValue(String::class.java)
                        TransactionData(title, amount, date, expenseType, receiptUrl)
                    } catch (e: Exception) {
                        null
                    }
                }

                val badges = evaluateBadges(transactions)
                trySend(badges)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun resolveIcon(name: String): ImageVector = when (name) {
        "trophy"    -> Icons.Default.EmojiEvents
        "fire"      -> Icons.Default.LocalFireDepartment
        "star"      -> Icons.Default.Star
        "lock"      -> Icons.Default.Lock
        "phone"     -> Icons.Default.PhoneAndroid
        "diamond"   -> Icons.Default.Diamond
        else        -> Icons.Default.EmojiEvents
    }

    // Evaluates all badge conditions against the transaction list
    private fun evaluateBadges(transactions: List<TransactionData>): List<Badge> {
        val total       = transactions.size
        val incomeList  = transactions.filter { it.expenseType.equals("Income", ignoreCase = true) }
        val hasReceipt  = transactions.any { !it.receiptUrl.isNullOrBlank() }
        val maxAmount   = transactions.maxOfOrNull { it.amount } ?: 0.0
        val streakDays  = calculateStreak(transactions)
        val streakWeeks = streakDays / 7
        syncUserStreakToLeaderboard(uid = "pass_current_user_uid_here", currentStreak = streakWeeks)
        return listOf(
            Badge(
                id          = "first_step",
                icon        = Icons.Default.Star,
                title       = "First Step",
                description = "Logged your first transaction",
                isUnlocked  = total >= 1
            ),
            Badge(
                id          = "getting_tracked",
                icon        = Icons.Default.TrendingUp,
                title       = "Getting Tracked",
                description = "Logged 10 transactions",
                isUnlocked  = total >= 10
            ),
            Badge(
                id          = "century_club",
                icon        = Icons.Default.EmojiEvents,
                title       = "Century Club",
                description = "Logged 100 transactions",
                isUnlocked  = total >= 100
            ),
            Badge(
                id          = "income_earner",
                icon        = Icons.Default.MonetizationOn,
                title       = "Income Earner",
                description = "Logged your first income",
                isUnlocked  = incomeList.isNotEmpty()
            ),
            Badge(
                id          = "big_spender",
                icon        = Icons.Default.Diamond,
                title       = "Big Spender",
                description = "Made a transaction over R1000",
                isUnlocked  = maxAmount >= 1000.0
            ),
            Badge(
                id          = "receipt_keeper",
                icon        = Icons.Default.Receipt,
                title       = "Receipt Keeper",
                description = "Attached a receipt to a transaction",
                isUnlocked  = hasReceipt
            ),
            Badge(
                id          = "on_a_roll",
                icon        = Icons.Default.LocalFireDepartment,
                title       = "On a Roll",
                description = "Logged transactions 3 days in a row",
                isUnlocked  = streakDays >= 3
            ),
            Badge(
                id          = "unstoppable",
                icon        = Icons.Default.DirectionsRun,
                title       = "Unstoppable",
                description = "Logged transactions 7 days in a row",
                isUnlocked  = streakDays >= 7
            )
        )
    }

    // Calculates the longest consecutive day streak from transaction dates
    private fun calculateStreak(transactions: List<TransactionData>): Int {
        if (transactions.isEmpty()) return 0

        // Get unique days as day-of-year strings "year-dayOfYear"
        val uniqueDays = transactions.map { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            val year = cal.get(Calendar.YEAR)
            val day  = cal.get(Calendar.DAY_OF_YEAR)
            year * 1000 + day // unique int per calendar day
        }.toSortedSet()

        var maxStreak     = 1
        var currentStreak = 1

        val dayList = uniqueDays.toList()
        for (i in 1 until dayList.size) {
            // Days are consecutive if they differ by 1 (handles year boundary via the *1000 encoding)
            if (dayList[i] - dayList[i - 1] == 1) {
                currentStreak++
                if (currentStreak > maxStreak) maxStreak = currentStreak
            } else {
                currentStreak = 1
            }
        }

        return maxStreak
    }

    // Internal data class — only used inside this repository
    private data class TransactionData(
        val title: String,
        val amount: Double,
        val date: Long,
        val expenseType: String,
        val receiptUrl: String?
    )

    fun syncUserStreakToLeaderboard(uid: String, currentStreak: Int) {
        // Reference a global top-level leaderboard node instead of the private user node
        val leaderboardRef = firebaseDatabase.reference.child("leaderboard").child(uid)

        // Fetch the current user profile metadata to attach a friendly display name
        firebaseDatabase.reference.child("users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                val firstName = snapshot.child("firstName").getValue(String::class.java) ?: "Anonymous"
                val lastName = snapshot.child("lastName").getValue(String::class.java) ?: "User"
                val fullName = "$firstName ${lastName.take(1)}."

                val leaderboardData = mapOf(
                    "uid" to uid,
                    "displayName" to fullName,
                    "streakWeeks" to currentStreak,
                    "lastUpdated" to System.currentTimeMillis()
                )

                leaderboardRef.setValue(leaderboardData)
            }
    }


}
