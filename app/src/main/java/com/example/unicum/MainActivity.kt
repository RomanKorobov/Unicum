package com.example.unicum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unicum.data.Drink
import com.example.unicum.ui.theme.BackCard1
import com.example.unicum.ui.theme.BackCard2
import com.example.unicum.ui.theme.BackPrice1
import com.example.unicum.ui.theme.BackPrice2
import com.example.unicum.ui.theme.BackPrice3
import com.example.unicum.ui.theme.TextName


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, "fragmentDrinks") {
        composable("FragmentDrinks") { FragmentDrinks() }
        composable("FragmentSettings/{id}") { navBackStackEntry ->
            FragmentSettings(navBackStackEntry.arguments?.getLong("id") ?: -1)
        }
    }
}

@Composable
fun FragmentDrinks() {
    val itemList = listOf(Drink(1, "Амаретто", "199", false, R.drawable.with_cream, "0.33"))
    val priceGradient = Brush.linearGradient(
        colors = listOf(BackPrice1, BackPrice2, BackPrice3),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )
    val cardGradient = Brush.linearGradient(
        colors = listOf(BackCard1, BackCard2),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )
    LazyVerticalGrid(
        columns = GridCells.FixedSize(227.dp),
        contentPadding = PaddingValues(9.5.dp, 9.5.dp)
    ) {
        items(itemList) { drink ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(cardGradient)) {
                Box(
                    Modifier
                        .width(227.dp)
                        .height(313.dp)
                        .clickable { }.clip(RoundedCornerShape(16.dp))
                ) {

                Image(
                    painterResource(id = R.drawable.with_cream),
                    null,
                    modifier = Modifier
                        .height(166.dp)
                        .width(166.dp),
                )
                Text(text = drink.name,
                    Modifier
                        .height(57.dp)
                        .width(227.dp), fontSize = 17.sp, textAlign = TextAlign.Center)
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .width(227.dp)
                                .background(priceGradient)
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(text = drink.volume,
                                Modifier
                                    .weight(1f)
                                    .padding(15.dp), TextName, fontSize = 16.sp, textAlign = TextAlign.Start)
                            if (!drink.free) Text(text = drink.price,
                                Modifier
                                    .weight(1f)
                                    .padding(15.dp), TextName, fontSize = 18.sp, textAlign = TextAlign.End)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun FragmentSettings(id: Long) {

}

@Preview(showBackground = true)
@Composable
fun DrinksPreview() {
    FragmentDrinks()
}