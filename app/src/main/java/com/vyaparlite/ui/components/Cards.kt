package com.vyaparlite.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vyaparlite.ui.theme.AppRadius
import com.vyaparlite.ui.theme.AppSpacing
import com.vyaparlite.ui.theme.BrandBlue
import com.vyaparlite.ui.theme.BrandGreen
import com.vyaparlite.ui.theme.CardSurface

@Composable
fun MetricCard(title: String, value: String, helper: String = "") {
    Card(
        shape = RoundedCornerShape(AppRadius.lg),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(listOf(BrandGreen.copy(alpha = 0.08f), BrandBlue.copy(alpha = 0.1f)))
                )
                .padding(AppSpacing.md)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(title, style = MaterialTheme.typography.labelLarge)
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (helper.isNotBlank()) Text(helper, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuickActionTile(label: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(AppRadius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(BrandGreen.copy(alpha = 0.12f), BrandBlue.copy(alpha = 0.08f))))
                .padding(AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp))
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun StockIndicatorBar(current: Int, threshold: Int) {
    val ratio = if (threshold <= 0) 1f else (current.toFloat() / (threshold * 2f)).coerceIn(0f, 1f)
    val animated = animateFloatAsState(targetValue = ratio, label = "stock").value
    val color = when {
        current <= threshold -> Color(0xFFEF4444)
        ratio < 0.6f -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFE2E8F0), RoundedCornerShape(99.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(8.dp)
                .background(color, RoundedCornerShape(99.dp))
        )
    }
}

@Composable
fun MiniBarChart(values: List<Float>, labels: List<String>, modifier: Modifier = Modifier) {
    val max = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppRadius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val barWidth = size.width / (values.size.coerceAtLeast(1) * 2f)
                values.forEachIndexed { i, value ->
                    val h = (value / max) * size.height
                    val left = (i * 2 + 1) * barWidth
                    drawRect(
                        brush = Brush.verticalGradient(listOf(BrandBlue, BrandGreen)),
                        topLeft = Offset(left, size.height - h),
                        size = Size(barWidth, h)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                labels.forEach { Text(it, style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}
