package com.zaurh.coinway.presentation.screen.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zaurh.coinway.presentation.components.CoinListItem
import com.zaurh.coinway.presentation.components.MySearchBar
import com.zaurh.coinway.presentation.viewmodel.MainViewModel
import com.zaurh.coinway.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "SuspiciousIndentation",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController
) {

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = viewModel.isRefreshing.value
    )

    val coinList = viewModel.coinList.value


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Coinway", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.background)
                ),
            )
        },
        content = {
            SwipeRefresh(state = swipeRefreshState, onRefresh = {
                viewModel.refreshCoinList()
            }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
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
                                .padding(10.dp),
                            onSearch = { viewModel.searchCoins(it) }
                        )
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(coinList) { coin ->
                                CoinListItem(coinList = coin) {
                                    navController.navigate("detail_screen/${coin.id}")
                                }
                            }
                        }
                    }
                }
            }
        },
    )

}