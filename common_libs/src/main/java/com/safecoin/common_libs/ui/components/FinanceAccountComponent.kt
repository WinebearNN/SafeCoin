package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.safecoin.common_libs.ui.text.TextHeader
import com.safecoin.common_libs.ui.text.TextMedium
import com.safecoin.common_libs.R.color as R_color
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.drawable as R_drawable


@Composable
fun FinanceAccountComponent(
      modifier: Modifier = Modifier,
      balance: String,

) {

      val gradient = Brush.horizontalGradient(
            colors = listOf(
                  Color(0xFFBCCBEE),
                  Color(0xFF65BAE3)
            )
      )

      Row(
            modifier = modifier
                  .fillMaxWidth()
                  .height(
                        height = dimensionResource(
                              id = R_dimen.common_component_height_large
                        )
                  )
                  .background(
                        brush = gradient,
                        shape = RoundedCornerShape(
                              size = dimensionResource(
                                    id = R_dimen.common_radius
                              )
                        )
                  ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
      ) {

            Column(
                  modifier = Modifier
                        .weight(1f)
            ) {

                  TextMedium(
                        modifier = Modifier
                              .padding(
                                    top = dimensionResource(
                                          id = R_dimen.common_vertical_padding_24
                                    ),
                                    start = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_16
                                    )
                              ),
                        text = "Current Balance",
                        colorId = R_color.text_medium,
                  )

                  TextHeader(
                        modifier = Modifier
                              .padding(
                                    vertical = dimensionResource(
                                          R_dimen.common_vertical_padding_16
                                    ),
                                    horizontal = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_16
                                    )
                              ),
                        text = balance
                  )
            }

            IconButton(
                  modifier = Modifier
                        .padding(
                              end = dimensionResource(
                                    R_dimen.common_horizontal_padding_16
                              )
                        )
                        .size(
                              dimensionResource(
                                    id = R_dimen.common_icon_size_medium
                              )
                        ),
                  onClick = {}
            ) {
                  IconComponent(
                        icon = painterResource(
                              id = R_drawable.ic_card_outline
                        )
                  )
            }

      }

}

@Preview(
      showBackground = false,
      showSystemUi = true,
      backgroundColor = 0xFF000000
)
@Composable
fun PreviewFinanceAccountComponent() {
      FinanceAccountComponent(
            balance = "$3 456 786"
      )
}