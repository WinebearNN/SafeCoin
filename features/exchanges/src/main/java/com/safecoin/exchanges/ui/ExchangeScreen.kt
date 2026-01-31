package com.safecoin.exchanges.ui

import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.safecoin.common_libs.ui.components.Divider
import com.safecoin.common_libs.ui.components.ListItemComponent
import com.safecoin.common_libs.ui.text.TextHeader
import com.safecoin.common_libs.ui.text.TextMain
import com.safecoin.common_libs.ui.text.TextMedium
import com.safecoin.exchanges.R.drawable
import com.safecoin.exchanges.R.drawable as R_drawable


@Composable
fun ExchangeComponent() {

      Column(
            modifier = Modifier
//                  .padding(
//                        horizontal = 16.dp
//                  )
//                  .background(Color.Black)

      ) {
            TextMain(
                  modifier = Modifier
                        .padding(
                              horizontal = 16.dp
                        ),
                  text = "Exchanges rate",
                  )

            repeat(3) {
                  ListItemComponent(
                        modifier = Modifier
                              .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                              ),
                        mainText = "CNY",
                        leadIcon = painterResource(
                              id = R_drawable.china_cny
                        ),
                        backgroundColor = null,
                        trimText = "67₽"
                  )
            }
      }
}