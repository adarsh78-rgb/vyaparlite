package com.vyaparlite.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import com.vyaparlite.ui.theme.BrandBlue
import com.vyaparlite.ui.theme.BrandGreen

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    val logoScale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, animationSpec = tween(650))
        kotlinx.coroutines.delay(900)
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(BrandGreen, BrandBlue))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Vyapar Lite",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.scale(logoScale.value)
        )
        Text(
            text = "Smart Business. Simple Management.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
