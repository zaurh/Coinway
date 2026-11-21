package com.zaurh.coinway.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/** Gently pulsing skeleton block. */
fun Modifier.skeleton(shape: Shape? = null): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "skeletonAlpha",
    )
    val resolved = shape ?: MaterialTheme.shapes.small
    graphicsLayer { this.alpha = alpha }
        .background(MaterialTheme.colorScheme.surfaceContainerHighest, resolved)
}

@Composable
fun CoinRowPlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).skeleton(CircleShape))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Box(Modifier.width(110.dp).height(14.dp).skeleton())
            Spacer(Modifier.height(8.dp))
            Box(Modifier.width(56.dp).height(10.dp).skeleton())
        }
        Box(Modifier.size(width = 60.dp, height = 24.dp).skeleton())
        Spacer(Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            Box(Modifier.width(80.dp).height(14.dp).skeleton())
            Spacer(Modifier.height(8.dp))
            Box(Modifier.width(48.dp).height(10.dp).skeleton())
        }
    }
}
