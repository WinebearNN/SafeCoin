package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.safecoin.common_libs.ui.text.TextMinor
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.drawable as R_drawable

@Composable
fun BadgeComponent(
      modifier: Modifier = Modifier,
      name: String,
      icon: Painter? = null,
) {
      Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
      ) {
            IconComponent(
                  modifier = Modifier
                        .size(
                              size = dimensionResource(
                                    R_dimen.common_icon_size
                              )
                        ),
                  icon = icon ?: painterResource(R_drawable.person),

            )


            TextMinor(
                  modifier = Modifier
                  .padding(
                    top = dimensionResource(
                          R_dimen.common_vertical_padding_8
                    )
                    ),
                  text = name
            )

      }
}

@Preview(
      showBackground = true,
      showSystemUi = true,
      backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewBadgeComponent() {
      BadgeComponent(
            name = "Владимир З."
      )
}