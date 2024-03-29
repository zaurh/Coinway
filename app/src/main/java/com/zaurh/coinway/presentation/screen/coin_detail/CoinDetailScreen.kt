package com.zaurh.coinway.presentation.screen.coin_detail

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.zaurh.coinway.R
import com.zaurh.coinway.common.addCommas
import com.zaurh.coinway.presentation.components.chart.ChartViewModel
import com.zaurh.coinway.presentation.components.chart.LineChart
import com.zaurh.coinway.presentation.viewmodel.CoinDetailViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(
    navController: NavController,
    coinDetailViewModel: CoinDetailViewModel = hiltViewModel(),
    chartViewModel: ChartViewModel = hiltViewModel(),
) {
    val coinItem = coinDetailViewModel.coin.value
    val error = coinDetailViewModel.coin.value.error
    val isLoading = coinDetailViewModel.coin.value.isLoading

    val chartsState = chartViewModel.chart.value
    val convertedList: List<Pair<Long, Double>> = chartsState.chart?.prices?.map { innerList ->
        val longValue = innerList[0].toLong()
        val doubleValue = innerList[1].toDouble()
        longValue to doubleValue
    } ?: emptyList()

    val dayStartPrice = convertedList.map { it.second }.firstOrNull()
    val dayEndPrice = convertedList.map { it.second }.lastOrNull()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                        Text(text = coinItem.coin?.name ?: "", color = Color.White)
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    colorResource(id = R.color.background)
                )
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(colorResource(id = R.color.background))
            ) {
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.background))
                ) {
                    coinItem.coin?.let {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .background(colorResource(id = R.color.background))
                                .padding(10.dp)
                        ) {
                            Row(
                                Modifier
                                    .background(colorResource(id = R.color.background))
                                    .fillMaxWidth()
                                    .height(150.dp), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = coinItem.coin.image.large),
                                    contentDescription = "",
                                    modifier = Modifier.size(100.dp)
                                )
                                Spacer(modifier = Modifier.size(15.dp))
                                Text(
                                    text = "$${coinItem.coin.market_data.current_price.usd}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 35.sp
                                )
                            }
                            Spacer(modifier = Modifier.size(40.dp))

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(colorResource(id = R.color.background))
                            ) {
                                chartsState.chart?.let {
                                    LineChart(
                                        data = convertedList,
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        startPrice = dayStartPrice!!.toDouble(),
                                        endPrice = dayEndPrice!!.toDouble()
                                    )
                                }


                            }
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Rank", color = Color.White)
                                    Text(
                                        text = "${coinItem.coin.market_data.market_cap_rank}",
                                        color = Color.White
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Market Cap", color = Color.White)
                                    Text(
                                        text = "$" + coinItem.coin.market_data.market_cap.usd.addCommas(),
                                        color = Color.White
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Total Volume", color = Color.White)
                                    Text(
                                        text = "$" + coinItem.coin.market_data.total_volume.usd.addCommas(),
                                        color = Color.White
                                    )
                                }

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "24H Highest", color = Color.White)
                                    Text(
                                        text = "$" + coinItem.coin.market_data.high_24h.usd,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "24H Lowest", color = Color.White)
                                    Text(
                                        text = "$" + coinItem.coin.market_data.low_24h.usd,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.size(10.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val weekPrice =
                                        coinItem.coin.market_data.price_change_percentage_7d
                                    Text(text = "Compared to last week", color = Color.White)
                                    Text(
                                        text = "${weekPrice}%",
                                        color =
                                        if (weekPrice > 0) colorResource(id = R.color.myGreen)
                                        else colorResource(
                                            id = R.color.myRed
                                        )
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val twoWeeksPrice =
                                        coinItem.coin.market_data.price_change_percentage_14d
                                    Text(text = "Compared to last 2 weeks", color = Color.White)
                                    Text(
                                        text = "${twoWeeksPrice}%",
                                        color =
                                        if (twoWeeksPrice > 0) colorResource(id = R.color.myGreen)
                                        else colorResource(
                                            id = R.color.myRed
                                        )
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val monthPrice =
                                        coinItem.coin.market_data.price_change_percentage_30d
                                    Text(text = "Compared to last month", color = Color.White)
                                    Text(
                                        text = "${monthPrice}%",
                                        color =
                                        if (monthPrice > 0) colorResource(id = R.color.myGreen)
                                        else colorResource(
                                            id = R.color.myRed
                                        )
                                    )
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val yearPrice =
                                        coinItem.coin.market_data.price_change_percentage_1y
                                    Text(text = "Compared to last year", color = Color.White)
                                    Text(
                                        text = "${yearPrice}%",
                                        color =
                                        if (yearPrice > 0) colorResource(id = R.color.myGreen)
                                        else colorResource(
                                            id = R.color.myRed
                                        )
                                    )
                                }
                            }

                        }
                    }
                }
            }

            if (error.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error, color = Color.White)
                }
            }
        }
    )
}