package com.example.flipfinance.ui.screens.Achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flipfinance.ViewModel.AchievementsViewModel
import com.example.flipfinance.ViewModel.WeeklyActivity

/*
Title: Disclosure of AI Usage in my Assessment.
• Section: InputStreakDetailsScreen.
• AI Tool: Claude Sonnet 4.6
• Purpose/intention : Design and syntax implementation of streaks details screen.
• Date(s) 02/06/2026.
• https://claude.ai/share/943aa681-7632-451c-84c2-b814e218caae
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputStreakDetailScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Activity Log",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // ── Summary stat ──────────────────────────────────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${state.inputStreakWeeks}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.inputStreakWeeks == 1) "week" else "weeks",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            val qualifying = state.allWeeklyActivity.count { it.qualifies }
                            Text(
                                text = "$qualifying",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.primary
                            )
                            Text(
                                text = "qualifying\nweeks total",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            // ── Rule reminder ─────────────────────────────────────────────────────
            item {
                Text(
                    text = "A week qualifies when you log transactions on 3 or more different days.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // ── Table header ──────────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Week",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(2.2f)
                        )
                        allDays.forEach { day ->
                            Text(
                                text = day.take(1),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }

            // ── Rows: one per week ────────────────────────────────────────────────
            if (state.allWeeklyActivity.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions recorded yet.\nStart adding transactions to build your streak!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                items(state.allWeeklyActivity) { weekActivity ->
                    WeekActivityRow(
                        weekActivity = weekActivity,
                        allDays = allDays
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun WeekActivityRow(
    weekActivity: WeeklyActivity,
    allDays: List<String>
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (weekActivity.qualifies)
            colorScheme.primary.copy(alpha = 0.05f)
        else
            colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = if (weekActivity.qualifies) 0.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Week label
            Text(
                text = weekActivity.weekLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface,
                fontWeight = if (weekActivity.qualifies) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(2.2f),
                lineHeight = 16.sp
            )

            // Day dots
            allDays.forEach { day ->
                val hasActivity = day in weekActivity.daysWithTransactions
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (hasActivity) colorScheme.primary
                                else colorScheme.onSurface.copy(alpha = 0.07f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasActivity) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }

            // Qualifying badge
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                if (weekActivity.qualifies) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Qualifies",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Does not qualify",
                        tint = colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}