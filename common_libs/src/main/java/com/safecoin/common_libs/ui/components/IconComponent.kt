package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import com.safecoin.common_libs.R.dimen as R_dimen

@Composable
fun IconComponent(
      modifier: Modifier = Modifier,
      icon: Painter,
      contentDescription: String? = null,
) {

      Icon(
            modifier = modifier
                  .padding(
                        horizontal = dimensionResource(
                              id = R_dimen.common_horizontal_padding_8,
                        ),
                        vertical = dimensionResource(
                              id = R_dimen.common_vertical_padding_8,
                        ),
                  )
                  .size(
                        size = dimensionResource(R_dimen.common_icon_size),
                  ),
            painter = icon,
            contentDescription = contentDescription,
      )

}