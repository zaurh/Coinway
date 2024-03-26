package com.zaurh.coinway.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.zaurh.coinway.R
import com.zaurh.coinway.domain.model.CoinList
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun CoinListItem(
    coinList: CoinList,
    onClick: () -> Unit
) {
    val focus = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        Row(Modifier.clickable {
            focus.clearFocus()
            onClick()
        }) {
            Row(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.background))
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(50.dp),
                    painter = rememberImagePainter(data = coinList.image),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = coinList.market_cap_rank.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(colorResource(id = R.color.search_background))
                                .padding(start = 3.dp, end = 3.dp)
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(text = coinList.name, color = Color.White)
                    }

                    Text(
                        text = coinList.symbol.uppercase(),
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "${coinList.current_price}$",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${roundOffDecimal(coinList.price_change_percentage_24h)}%",
                        color = if (coinList.price_change_percentage_24h > 0)
                            colorResource(id = R.color.myGreen) else colorResource(id = R.color.myRed)
                    )
                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        text = if (coinList.price_change_percentage_24h > 0) "▲" else "▼",
                        fontWeight = FontWeight.Bold,
                        color = if (coinList.price_change_percentage_24h > 0)
                            colorResource(id = R.color.myGreen) else colorResource(id = R.color.myRed),
                        fontSize = 22.sp
                    )

                }
            }
        }

    }
}


fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}