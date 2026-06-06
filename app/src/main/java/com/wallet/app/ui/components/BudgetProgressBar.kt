package com.wallet.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wallet.app.ui.theme.*

@Composable
fun BudgetProgressBar(
    categoryName: String,
    categoryEmoji: String,
    spent: Double,
    budget: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "budgetProgress"
    )
    val isOverspent = spent > budget
    val progressColor = when {
        isOverspent -> ExpenseRed
        progress > 0.8f -> Amber500
        else -> color
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = categoryEmoji, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$${String.format("%.0f", spent)} / $${String.format("%.0f", budget)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .then(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
        ) {
            // Background
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {}
            // Progress
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress),
                shape = RoundedCornerShape(4.dp),
                color = progressColor
            ) {}
        }
        if (isOverspent) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\u26A0\uFE0F Overspent by $${String.format("%.2f", spent - budget)}",
                style = MaterialTheme.typography.labelSmall,
                color = ExpenseRed
            )
        }
    }
}
