package com.example.zaurscoin.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.zaurscoin.R
import com.example.zaurscoin.presentation.MainViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val coin = viewModel.coin.value
    val chartsState = viewModel.chart.observeAsState()

    val convertedList: List<Pair<Long, Double>> = chartsState.value?.prices?.map { innerList ->
        val longValue = innerList[0].toLong()
        val doubleValue = innerList[1].toDouble()
        longValue to doubleValue
    } ?: emptyList()

    val dayStartPrice = convertedList.map { it.second }.firstOrNull()
    val dayEndPrice = convertedList.map { it.second }.lastOrNull()



    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                    Text(text = coin?.name ?: "...", color = Color.White)
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }


            }, backgroundColor = colorResource(id = R.color.background))
        }
    ) {
        coin?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.background))
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.background))
                        .padding(10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberImagePainter(data = coin.image.large),
                            contentDescription = "",
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = coin.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(text = coin.symbol, color = Color.White)
                            Text(
                                text = coin.market_data.current_price.usd.toString(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                        }
                    }
                    Divider()
                    Spacer(modifier = Modifier.size(40.dp))

                    Row(Modifier.fillMaxWidth()) {
                        chartsState?.let {
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

                }

            }
        }

    }
}