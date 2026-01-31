package com.safecoin.common_libs.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun IconComponent(
      modifier: Modifier = Modifier,
      icon: Painter,
      contentDescription: String? = null,
) {

      Icon(
            modifier = modifier,
            painter = icon,
            contentDescription = contentDescription,
            tint = Color.Unspecified,
      )

}