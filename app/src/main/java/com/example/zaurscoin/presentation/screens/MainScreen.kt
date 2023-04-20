package com.example.zaurscoin.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.zaurscoin.presentation.MainViewModel
import com.example.zaurscoin.presentation.components.CoinListItem
import com.example.zaurscoin.presentation.components.MySearchBar
import com.example.zaurscoin.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController
) {
    val coinList = viewModel.coinList.value

    Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "My Coins", color = Color.White)}, backgroundColor = colorResource(id = R.color.background))
            },
            content = {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.background))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MySearchBar(
                            modifier = Modifier
                                .background(colorResource(id = R.color.background))
                                .padding(5.dp),
                            onSearch = {viewModel.searchCoins(it)}
                        )
                        Divider()

                        LazyColumn(modifier = Modifier.weight(1f)){
                            items(coinList){ coin ->
                                CoinListItem(coinList = coin, navController)
                            }
                        }
                    }
                }
            },
            bottomBar = {

            }
        )

}