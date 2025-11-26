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
fun TextMain(
      modifier: Modifier = Modifier,
      text: String,
) {
      Text(
            modifier = modifier,
            text = text,
            fontSize = dimensionResource(
                  id = R_dimen.common_main_text_size_small,
            ).value.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            letterSpacing = dimensionResource(
                  id = R_dimen.common_letter_spacing_small,
            ).value.sp,
            color = colorResource(
                  id = R_color.text_main,
            ),
      )
}