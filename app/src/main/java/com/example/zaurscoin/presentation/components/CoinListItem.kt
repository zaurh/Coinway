package com.example.zaurscoin.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.zaurscoin.domain.model.CoinList
import java.math.RoundingMode
import java.text.DecimalFormat
import com.example.zaurscoin.R

@Composable
fun CoinListItem(
    coinList: CoinList,
    navController: NavController
) {
    Card(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.clickable {
            navController.navigate("detail_screen/${coinList.id}")
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
                    Text(text = coinList.name, color = Color.White)
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
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}