package com.safecoin.common_libs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safecoin.common_libs.R.color as R_color
import com.safecoin.common_libs.R.drawable as R_drawable
import com.safecoin.common_libs.R.dimen as R_dimen




@Composable
fun ListItemComponent(
      leadIcon: Painter? = null,
      mainText: String,
      minorText: String? = null,
      trimText: String? = null,
) {
      Box(
            modifier = Modifier
                  .fillMaxWidth()
                  .height(
                        height = dimensionResource(R_dimen.common_height)
                  )
                  .clip(
                        shape = RoundedCornerShape(
                              size = dimensionResource(R_dimen.common_radius)
                        )
                  )
                  .background(
                        color = colorResource(
                              id = R_color.on_surface
                        ),
                  ),
            contentAlignment = Alignment.CenterStart
      ) {

            Row (
                  modifier = Modifier
                        .fillMaxSize(),
                  verticalAlignment = Alignment.CenterVertically,
            ) {

                  if (leadIcon != null) {

                        Icon(
                              modifier = Modifier
                                    .padding(
                                          start = dimensionResource(R_dimen.common_horizontal_padding),
                                          end = dimensionResource(R_dimen.common_horizontal_padding),
                                    )
                                    .size(
                                          size = dimensionResource(R_dimen.common_icon_size),
                                    ),
                              painter = leadIcon,
                              contentDescription = null,
                        )

                  }

                  if (minorText.isNullOrBlank()) {

                        Text(
                              text = mainText,
                              fontSize = dimensionResource(R_dimen.common_main_text_size_medium).value.sp,
                              fontStyle = FontStyle.Normal,
                              fontWeight = FontWeight.Medium
                        )

                  } else {

                        Column {

                              Text(
                                    text = mainText,
                                    fontSize = dimensionResource(R_dimen.common_main_text_size_small).value.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = dimensionResource(R_dimen.common_letter_spacing_small).value.sp,
                                    color = colorResource(R_color.text_main)
                              )

                              Text(
                                    text = minorText,
                                    fontSize = dimensionResource(R_dimen.common_minor_text_size_large).value.sp,
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R_color.text_minor),
                              )

                        }

                  }

                  if (!trimText.isNullOrBlank()) {
                        Text(
                              modifier = Modifier
                                    .padding(
                                          end = dimensionResource(R_dimen.common_horizontal_padding)
                                    )
                                    .weight(
                                          weight = 1f,
                                    ),
                              text = trimText,
                              textAlign = TextAlign.End,
                              fontSize = dimensionResource(R_dimen.common_main_text_size_medium).value.sp,
                              letterSpacing = dimensionResource(R_dimen.common_letter_spacing_small).value.sp,
                              color = colorResource(R_color.text_price_outcome)
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
            trimText = "673$"
      )
}