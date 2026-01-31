package com.safecoin.common_libs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.safecoin.common_libs.R.color as R_color


@Composable
fun Divider(
      modifier: Modifier = Modifier,
) {
      Box(
            modifier = modifier
                  .height(1.dp)
                  .fillMaxWidth()
                  .background(
                        color = colorResource(
                              R_color.black
                        )
                  )
      )
}