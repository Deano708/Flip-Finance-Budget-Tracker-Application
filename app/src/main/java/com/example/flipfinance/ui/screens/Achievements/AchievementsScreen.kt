package com.example.flipfinance.ui.screens.Achievements


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.AchievementsViewModel
import com.example.flipfinance.ViewModel.Badge

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: AchievementsScreen.
• AI Tool: Claude Sonnet 4.6
• Purpose/intention : Design and syntax implementation of Achievements screen, allowing for navigation to input streak details.
• Date(s) 02/06/2026.
• https://claude.ai/share/943aa681-7632-451c-84c2-b814e218caae
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onNavigateToStreakDetail: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── CARD 1: Input Streak ──────────────────────────────────────────────
            InputStreakCard(
                streakWeeks = state.inputStreakWeeks,
                weeklyDays = state.weeklyTransactionDays,
                onCardClick = onNavigateToStreakDetail
            )

            // ── CARD 2: App Open Streak ───────────────────────────────────────────
            AppOpenStreakCard(
                streakWeeks = state.appOpenStreakWeeks
            )

            // ── CARD 3: Badges ────────────────────────────────────────────────────
            BadgesCard(
                badges = state.badges
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ── Card 1: Input Streak ──────────────────────────────────────────────────────

@Composable
private fun InputStreakCard(
    streakWeeks: Int,
    weeklyDays: Map<String, Boolean>,
    onCardClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Cap progress at a 12-week milestone for the progress bar
    val streakMilestone = 12
    val progress = (streakWeeks.toFloat() / streakMilestone).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Input Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "3+ transactions per week",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                // Tap indicator
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Week streak count display
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$streakWeeks",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (streakWeeks > 0) colorScheme.primary else colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (streakWeeks == 1) "week" else "weeks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "/$streakMilestone weeks",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = colorScheme.primary,
                trackColor = colorScheme.primary.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current week day bubbles
            Text(
                text = "This week",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyDays.forEach { (day, hasTransaction) ->
                    DayBubble(
                        label = day,
                        isActive = hasTransaction,
                        activeColor = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tap to view your full activity log →",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DayBubble(label: String, isActive: Boolean, activeColor: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else colorScheme.onSurface.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.take(1), // Just show "M", "T", etc.
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) activeColor else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Card 2: App Open Streak ───────────────────────────────────────────────────

@Composable
private fun AppOpenStreakCard(streakWeeks: Int) {
    val colorScheme = MaterialTheme.colorScheme
    val streakMilestone = 8
    val progress = (streakWeeks.toFloat() / streakMilestone).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.secondary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "App Engagement Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Open the app 3+ times per week",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Streak count
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$streakWeeks",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (streakWeeks > 0) colorScheme.secondary else colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (streakWeeks == 1) "week" else "weeks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "/$streakMilestone weeks",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = colorScheme.secondary,
                trackColor = colorScheme.secondary.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informational note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colorScheme.secondaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(16.dp).padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open FlipFinance at least 3 times each week to keep your streak alive. " +
                                "Each qualifying week adds one to your total.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSecondaryContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ── Card 3: Badges ────────────────────────────────────────────────────────────

@Composable
private fun BadgesCard(badges: List<Badge>) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.tertiary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = colorScheme.tertiary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Badges",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Earned through unique achievements",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Coming Soon banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Construction,
                        contentDescription = null,
                        tint = colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Coming soon — badges are being built",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badge grid — all locked/greyed out as placeholders
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(badges) { badge ->
                    BadgeItem(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge) {
    val colorScheme = MaterialTheme.colorScheme
    val icon = getBadgeIcon(badge.iconName)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (badge.isEarned)
                        colorScheme.tertiary.copy(alpha = 0.15f)
                    else
                        colorScheme.onSurface.copy(alpha = 0.06f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = badge.title,
                tint = if (badge.isEarned) colorScheme.tertiary
                else colorScheme.onSurface.copy(alpha = 0.25f),
                modifier = Modifier.size(26.dp)
            )
            // Lock overlay for unearned badges
            if (!badge.isEarned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = badge.title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = if (badge.isEarned) colorScheme.onSurface
            else colorScheme.onSurface.copy(alpha = 0.35f),
            maxLines = 2
        )
    }
}

// ── Icon resolver for badges ──────────────────────────────────────────────────
private fun getBadgeIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Payments"             -> Icons.Default.Payments
        "TrendingUp"           -> Icons.Default.TrendingUp
        "Star"                 -> Icons.Default.Star
        "LocalFireDepartment"  -> Icons.Default.LocalFireDepartment
        "Savings"              -> Icons.Default.Savings
        "AttachMoney"          -> Icons.Default.AttachMoney
        "Category"             -> Icons.Default.Category
        "Receipt"              -> Icons.Default.Receipt
        else                   -> Icons.Default.EmojiEvents
    }
}