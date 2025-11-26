package com.safecoin.common_libs.ui.text

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.safecoin.common_libs.R.dimen as R_dimen
import com.safecoin.common_libs.R.color as R_color

@Composable
fun TextMedium(
      modifier: Modifier = Modifier,
      text: String,
      colorId: Int = 0,
) {
      Text(
            modifier = modifier
                  .padding(
                        end = dimensionResource(
                              id = R_dimen.common_horizontal_padding_8,
                        ),
                  ),
            text = text,
            textAlign = TextAlign.End,
            fontSize = dimensionResource(
                  id = R_dimen.common_main_text_size_small,
            ).value.sp,
            letterSpacing = dimensionResource(
                  id = R_dimen.common_letter_spacing_small,
            ).value.sp,
            color = colorResource(
                  id = if (colorId == 0) R_color.black else colorId,
            ),
            fontWeight = FontWeight.Medium
      )
}