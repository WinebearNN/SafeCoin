package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.safecoin.common_libs.ui.text.TextMain
import com.safecoin.common_libs.ui.text.TextMedium
import com.safecoin.common_libs.ui.text.TextMinor
import com.safecoin.common_libs.R.color as R_color
import com.safecoin.common_libs.R.drawable as R_drawable
import com.safecoin.common_libs.R.dimen as R_dimen

@Composable
fun GridItemComponent(
      modifier: Modifier = Modifier,
      icon: Painter? = null,
      mainText: String? = null,
      mediumText: String? = null,
      minorText: String? = null,
) {

      Column(
            modifier = modifier
                  .aspectRatio(1f)
                  .clip(
                        shape = RoundedCornerShape(
                              size = dimensionResource(R_dimen.common_radius),
                        )
                  )
                  .background(
                        color = colorResource(
                              id = R_color.on_surface,
                        ),
                  ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
      ) {

            if (icon != null) {

                  IconComponent(
                        icon = icon,
                  )

            }

            if (mainText != null) {

                  TextMain(
                        text = mainText,
                        modifier = Modifier
                              .padding(
                                    start = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_12,
                                    ),
                                    top = dimensionResource(
                                          id = R_dimen.common_vertical_padding_16
                                    )
                              )
                  )

            }

            if (mediumText != null) {

                  TextMedium(
                        text = mediumText,
                        modifier = Modifier
                              .padding(
                                    start = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_12,
                                    ),
                                    top = dimensionResource(
                                          id = R_dimen.common_vertical_padding_8
                                    )
                              )
                  )

            }

            if (minorText != null) {

                  TextMinor(
                        modifier = Modifier
                              .padding(
                                    start = dimensionResource(
                                          id = R_dimen.common_horizontal_padding_12,
                                    ),
                                    top = dimensionResource(
                                          id = R_dimen.common_vertical_padding_8
                                    )
                              ),
                        text = minorText,
                        colorId = R_color.black
                  )

            }

      }
}

@Preview(
      showBackground = true,
      showSystemUi = true,
      backgroundColor = 0xFF000000
)
@Composable
fun PreviewGridItemComponent() {
      Row(
            modifier = Modifier
                  .padding(
                        top = 100.dp,
                        bottom = 100.dp,
                  )
                  .fillMaxWidth()
                  .height(200.dp)
      ) {
            repeat(2) {
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
                        icon = painterResource(
                              id = R_drawable.apple,
                        ),
                        mainText = "Apple Premium",
                        mediumText = "$30/month",
                        minorText = "2 days left"
                  )
            }
      }
}