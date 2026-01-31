package com.safecoin.main_screen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.safecoin.common_libs.ui.components.BadgeComponent
import com.safecoin.common_libs.ui.components.IconComponent
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.drawable as R_drawable

@Composable
fun Header(
      modifier: Modifier = Modifier,
) {

      Row(
            modifier = modifier
                  .fillMaxWidth()
                  .height(
                        height = dimensionResource(
                              id = R_dimen.common_component_height_medium
                        )
                  )
                  .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
      ) {

            Box(
                  modifier = Modifier
                        .weight(1f),
                  contentAlignment = Alignment.CenterStart
            ) {

                  IconComponent(
                        modifier = Modifier
                              .wrapContentHeight()
                              .wrapContentWidth(),
                        icon = painterResource(
                              id = R_drawable.ic_logo
                        ),
                  )
            }

            Box(
                  modifier = Modifier
                        .weight(1f),
                  contentAlignment = Alignment.BottomEnd
            ) {
                  BadgeComponent(
                        name = "Владимир З."
                  )
            }
      }
}

@Preview(
      showBackground = true,
      showSystemUi = true,
      backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewHeader() {
      Header()
}