package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safecoin.common_libs.ui.text.TextMain
import com.safecoin.common_libs.ui.text.TextMedium
import com.safecoin.common_libs.ui.text.TextMinor
import com.safecoin.common_libs.R.color as R_color
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.drawable as R_drawable


@Composable
fun ListItemComponent(
      modifier: Modifier = Modifier,
      leadIcon: Painter? = null,
      mainText: String,
      minorText: String? = null,
      trimText: String? = null,
) {
      Box(
            modifier = modifier
                  .fillMaxWidth()
                  .height(
                        height = dimensionResource(
                              id = R_dimen.common_height,
                        ),
                  )
                  .clip(
                        shape = RoundedCornerShape(
                              size = dimensionResource(
                                    id = R_dimen.common_radius,
                              ),
                        )
                  )
                  .background(
                        color = colorResource(
                              id = R_color.on_surface,
                        ),
                  ),
            contentAlignment = Alignment.CenterStart,
      ) {

            Row(
                  modifier = Modifier
                        .fillMaxSize(),
                  verticalAlignment = Alignment.CenterVertically,
            ) {

                  if (leadIcon != null) {

                        IconComponent(
                              icon = leadIcon,
                        )

                  }

                  if (minorText.isNullOrBlank()) {

                        Text(
                              text = mainText,
                              fontSize = dimensionResource(
                                    id = R_dimen.common_main_text_size_medium,
                              ).value.sp,
                              fontStyle = FontStyle.Normal,
                              fontWeight = FontWeight.Medium,
                        )

                  } else {

                        Column {

                              TextMain(
                                    text = mainText,
                              )

                              TextMinor(
                                    text = minorText
                              )

                        }

                  }

                  if (!trimText.isNullOrBlank()) {

                        TextMedium(
                              modifier = Modifier.weight(
                                    weight = 1f,
                              ),
                              text = trimText,
                              colorId = R_color.text_price_outcome,
                        )

                  }

            }

      }
}

@Preview(
      showBackground = true,
      showSystemUi = true,
      backgroundColor = 0xFF000000
)
@Composable
fun PreviewListItemComponent() {
      ListItemComponent(
            leadIcon = painterResource(
                  id = R_drawable.apple
            ),
            mainText = "Apple Inc.",
            minorText = "21 sep, 15:32",
            trimText = "-$673",
            modifier = Modifier
                  .padding(
                        vertical = 100.dp,
                  )
      )
}