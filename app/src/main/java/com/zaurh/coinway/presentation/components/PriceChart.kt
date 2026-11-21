package com.zaurh.coinway.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import com.zaurh.coinway.domain.model.PricePoint
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Area chart of [points]. Drag horizontally to scrub; [onScrub] receives the touched point,
 * or null when the finger lifts. Vertical drags are left to the scrolling parent.
 */
@Composable
fun PriceChart(
    points: List<PricePoint>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    guideColor: Color = lineColor.copy(alpha = 0.5f),
    onScrub: (PricePoint?) -> Unit = {},
) {
    val reveal = remember { Animatable(0f) }
    LaunchedEffect(points) {
        reveal.snapTo(0f)
        reveal.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }

    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }
    val haptics = LocalHapticFeedback.current
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val currentOnScrub by rememberUpdatedState(onScrub)

    val minPrice = remember(points) { points.minOfOrNull { it.price } ?: 0.0 }
    val maxPrice = remember(points) { points.maxOfOrNull { it.price } ?: 0.0 }

    Canvas(
        modifier = modifier.pointerInput(points) {
            if (points.size < 2) return@pointerInput
            fun select(x: Float) {
                val chartWidth = size.width - 10.dp.toPx()
                val index = ((x / chartWidth) * (points.size - 1)).roundToInt().coerceIn(0, points.lastIndex)
                if (index != selectedIndex) {
                    selectedIndex = index
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    currentOnScrub(points[index])
                }
            }
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var totalX = 0f
                var totalY = 0f
                var scrubbing = false
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    if (!scrubbing) {
                        if (change.isConsumed) break
                        val delta = change.positionChange()
                        totalX += delta.x
                        totalY += delta.y
                        if (abs(totalY) > touchSlop && abs(totalY) > abs(totalX)) break // vertical scroll
                        if (abs(totalX) > touchSlop) scrubbing = true
                    }
                    if (scrubbing) {
                        select(change.position.x)
                        change.consume()
                    }
                }
                selectedIndex = null
                currentOnScrub(null)
            }
        }
    ) {
        if (points.size < 2) return@Canvas
        val topInset = 12.dp.toPx()
        val bottomInset = 12.dp.toPx()
        val usableHeight = size.height - topInset - bottomInset
        val range = (maxPrice - minPrice).takeIf { it > 0 } ?: 1.0
        // Leave room on the right so the latest-price dot isn't clipped.
        val chartWidth = size.width - 10.dp.toPx()
        val stepX = chartWidth / points.lastIndex

        fun offsetOf(index: Int) = Offset(
            x = index * stepX,
            y = topInset + (usableHeight * (1 - (points[index].price - minPrice) / range)).toFloat(),
        )

        val line = Path().apply {
            val first = offsetOf(0)
            moveTo(first.x, first.y)
            for (i in 1..points.lastIndex) offsetOf(i).let { lineTo(it.x, it.y) }
        }
        val area = Path().apply {
            addPath(line)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        // Dashed baseline at the opening price of the range.
        val baseY = offsetOf(0).y
        drawLine(
            color = guideColor.copy(alpha = 0.35f),
            start = Offset(0f, baseY),
            end = Offset(size.width, baseY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
        )

        clipRect(right = size.width * reveal.value) {
            drawPath(
                path = area,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.28f), lineColor.copy(alpha = 0.02f), Color.Transparent),
                    startY = topInset,
                    endY = size.height,
                ),
            )
            drawPath(
                path = line,
                color = lineColor,
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        val index = selectedIndex
        if (index != null) {
            val point = offsetOf(index)
            drawLine(
                color = guideColor,
                start = Offset(point.x, 0f),
                end = Offset(point.x, size.height),
                strokeWidth = 1.dp.toPx(),
            )
            drawCircle(color = lineColor.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = point)
            drawCircle(color = lineColor, radius = 5.dp.toPx(), center = point)
        } else if (reveal.value >= 1f) {
            // Pulse-free "live" dot at the latest price.
            val last = offsetOf(points.lastIndex)
            drawCircle(color = lineColor.copy(alpha = 0.25f), radius = 7.dp.toPx(), center = last)
            drawCircle(color = lineColor, radius = 3.5.dp.toPx(), center = last)
        }
    }
}
