package com.safecoin.common_libs.ui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.safecoin.common_libs.R.color as R_color
import com.safecoin.common_libs.R.dimen as R_dimen

@Composable
fun TextMinor(
      modifier: Modifier = Modifier,
      text: String,
      colorId: Int = 0,
) {

      Text(
            modifier = modifier,
            text = text,
            fontSize = dimensionResource(
                  id = R_dimen.common_minor_text_size_large,
            ).value.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            color = colorResource(
                  id = if (colorId == 0) R_color.text_minor else colorId,
            ),
      )

}