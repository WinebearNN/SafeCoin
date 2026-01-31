package com.safecoin.safecoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.safecoin.main_screen.ui.MainScreen
import com.safecoin.safecoin.ui.theme.SafeCoinTheme

class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                  SafeCoinTheme {
                        MainScreen()
                  }
            }
      }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
      val j = colorResource(R.color.black)

      Text(
            text = "Hello $name!",
            modifier = modifier
      )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
      SafeCoinTheme {
            Greeting("Android")
      }
}