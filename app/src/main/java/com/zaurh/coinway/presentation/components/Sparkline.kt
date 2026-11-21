package com.zaurh.coinway.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Tiny 7-day trend line used in coin rows. */
@Composable
fun Sparkline(
    values: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val (min, max) = remember(values) {
        (values.minOrNull() ?: 0.0) to (values.maxOrNull() ?: 0.0)
    }
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val range = (max - min).takeIf { it > 0 } ?: 1.0
        val stepX = size.width / (values.size - 1)
        val inset = 2.dp.toPx()
        val usableHeight = size.height - inset * 2
        fun point(index: Int) = Offset(
            x = index * stepX,
            y = inset + (usableHeight * (1 - (values[index] - min) / range)).toFloat(),
        )

        val line = Path().apply {
            moveTo(point(0).x, point(0).y)
            for (i in 1 until values.size) point(i).let { lineTo(it.x, it.y) }
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = fill,
            brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.22f), Color.Transparent)),
        )
        drawPath(
            path = line,
            color = color,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
