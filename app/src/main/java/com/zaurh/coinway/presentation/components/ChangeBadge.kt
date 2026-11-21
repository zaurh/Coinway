package com.zaurh.coinway.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.zaurh.coinway.core.util.Formatters
import com.zaurh.coinway.presentation.theme.CoinwayTheme

/** "▲ 2.31%" pill tinted green or red. */
@Composable
fun ChangeBadge(
    change: Double?,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    val color = CoinwayTheme.marketColors.forChange(change)
    Row(
        modifier = modifier
            .then(
                if (filled) Modifier
                    .background(color.copy(alpha = 0.14f), MaterialTheme.shapes.small)
                    .padding(start = 2.dp, end = 8.dp, top = 3.dp, bottom = 3.dp)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (change != null && change != 0.0) {
            Icon(
                imageVector = if (change > 0) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = change?.let { Formatters.percent(it).trimStart('+', '−') } ?: "—",
            style = textStyle,
            color = color,
        )
    }
}
