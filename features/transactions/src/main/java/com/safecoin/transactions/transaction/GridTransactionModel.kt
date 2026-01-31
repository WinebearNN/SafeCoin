package com.safecoin.transactions.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.safecoin.common_libs.R.drawable as R_drawable
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.ui.components.GridItemComponent
import com.safecoin.common_libs.ui.components.IconComponent

@Composable
fun GridTransactionModel() {

      val gradient = Brush.horizontalGradient(
            colors = listOf(
                  Color(0xFFBCCBEE),
                  Color(0xFF65BAE3)
            )
      )

      Row() {

            Box(
                  modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
//                        .background(
//                              color = Color.Black
//                        ),
                          ,
                  contentAlignment = Alignment.Center
            ) {
                  IconComponent(
                        icon = painterResource(
                              id = R_drawable.ic_logo,
                        )
                  )
            }

            GridItemComponent(
                  modifier = Modifier
                        .padding(
                              vertical = dimensionResource(
                                    id = R_dimen.common_vertical_padding_8,
                              ),
                              horizontal = dimensionResource(
                                    id = R_dimen.common_horizontal_padding_8,
                              ),
                        )
                        .weight(
                              weight = 1f,
                        ),
//                        icon = painterResource(
//                              id = R_drawable.apple,
//                        ),
                  mainText = "Payments",
//                        mediumText = "Incomes"Incomes
                  minorText = "Analyze your \nsubs, consumptions \nand other ",
                  backgroundColor = Color(0xFF97D7F6)
            )

      }
}