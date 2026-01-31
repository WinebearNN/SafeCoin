package com.safecoin.main_screen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.safecoin.exchanges.R.drawable as R_drawable
import com.safecoin.common_libs.ui.components.FinanceAccountComponent
import com.safecoin.common_libs.ui.components.GridItemComponent
import com.safecoin.common_libs.ui.components.ListItemComponent
import com.safecoin.exchanges.ui.ExchangeComponent
import com.safecoin.transactions.transaction.GridTransactionModel
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.color as R_color


@Composable
fun MainScreen() {

      val gridState = rememberSaveable(saver = LazyGridState.Saver) {
            LazyGridState()
      }

      LazyColumn(
            modifier = Modifier
                  .fillMaxSize()
                  .background(color = colorResource(R_color.surface)),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
      ) {
            item {
                  Header(
                        modifier = Modifier
                              .padding(
                                    vertical = dimensionResource(
                                          id = R_dimen.common_vertical_padding_32,
                                    ),
                                    horizontal = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_16
                                    )
                              )
                  )
            }

            item {

                  FinanceAccountComponent(
                        modifier = Modifier
                              .padding(
                                    horizontal = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_16
                                    )
                              ),
                        balance = "$786"
                  )
            }

            item {
                  Row(
                        modifier = Modifier
                              .fillMaxWidth()
                              .wrapContentHeight()
                              .padding(
                                    vertical = 64.dp,
                                    horizontal = 24.dp
                              ),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center

                  ) {

                        GridTransactionModel()
                  }
            }

            item {
                  ExchangeComponent()
            }
      }
}


@Preview(
      showBackground = true,
      showSystemUi = true,
      backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewMainScreen() {
      MainScreen()
}