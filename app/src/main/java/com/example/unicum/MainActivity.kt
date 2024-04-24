package com.example.unicum

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unicum.data.Drink
import com.example.unicum.data.DrinksViewModel
import com.example.unicum.db.Database
import com.example.unicum.ui.theme.BackCard1
import com.example.unicum.ui.theme.BackCard2
import com.example.unicum.ui.theme.BackField
import com.example.unicum.ui.theme.BackPrice1
import com.example.unicum.ui.theme.BackPrice2
import com.example.unicum.ui.theme.BackPrice3
import com.example.unicum.ui.theme.ButtonColor
import com.example.unicum.ui.theme.ButtonTextColor
import com.example.unicum.ui.theme.FreeSellColor
import com.example.unicum.ui.theme.RubbleColor
import com.example.unicum.ui.theme.TextName
import com.example.unicum.ui.theme.WhiteTextName
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
        val sharedPrefs = this.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        lifecycleScope.launch {
            if (sharedPrefs.getBoolean("first", true)) {
                Database.fillDb(applicationContext)
                sharedPrefs.edit().putBoolean("first", false).apply()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel = DrinksViewModel()
    NavHost(navController, "fragmentDrinks") {
        composable("FragmentDrinks") { FragmentDrinks(navController) }
        composable("FragmentSettings/{id}") { navBackStackEntry ->
            FragmentSettings(navBackStackEntry.arguments?.getInt("id") ?: -1)
        }
    }
}

@Composable
fun FragmentDrinks(navController: NavController) {
    val viewModel = DrinksViewModel()
    val itemList = viewModel.drinksFlow.collectAsState()
    viewModel.getDrinks()
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
    Box(
        Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(6.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(250.dp),
            contentPadding = PaddingValues(12.dp, 12.dp)
        ) {
            items(itemList.value) { drink ->
                Box(
                    Modifier
                        .width(227.dp)
                        .height(313.dp)
                        .background(cardGradient)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { navController.navigate("FragmentSettings/${drink.id}") }
                ) {
                    Column {
                        Image(
                            painterResource(id = R.drawable.with_cream),
                            null,
                            modifier = Modifier
                                .height(166.dp)
                                .width(166.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = drink.name,
                            Modifier
                                .height(57.dp)
                                .fillMaxWidth(), TextName, fontSize = 17.sp, textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .width(227.dp)
                                    .background(priceGradient)
                                    .align(Alignment.BottomCenter)
                            ) {
                                Text(
                                    text = drink.volume,
                                    Modifier
                                        .weight(1f)
                                        .padding(15.dp),
                                    TextName,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Start
                                )
                                if (!drink.free) Text(
                                    text = drink.price,
                                    Modifier
                                        .weight(1f)
                                        .padding(15.dp),
                                    TextName,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun FragmentSettings(id: Int) {
    val viewModel = DrinksViewModel()
    val drinkState = viewModel.drink.collectAsState()
    viewModel.getDrinkById(id)
    Row(
        Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
    ) {
        Column(Modifier.width(418.dp)) {
            Text(text = stringResource(id = R.string.naimenovanie),
                Modifier
                    .width(418.dp)
                    .align(Alignment.Start), TextName, fontSize = 16.sp)
            Box(
                Modifier
                    .width(418.dp)
                    .height(52.dp)
                    .background(BackField)) {
                Text(text = drinkState.value?.name ?: "",
                    Modifier
                        .width(418.dp)
                        .padding(10.dp), WhiteTextName, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(id = R.string.price),
                Modifier
                    .width(418.dp)
                    .align(Alignment.Start), TextName, fontSize = 16.sp)
            Box(
                Modifier
                    .width(418.dp)
                    .height(52.dp)
                    .background(BackField)) {
                Text(text = drinkState.value?.price ?: "",
                    Modifier
                        .width(150.dp)
                        .padding(10.dp), WhiteTextName, fontSize = 20.sp)
                Text(text = stringResource(id = R.string.rubble),
                    Modifier
                        .width(50.dp)
                        .padding(10.dp), RubbleColor, fontSize = 20.sp, textAlign = TextAlign.End)
            }
            Box(
                modifier = Modifier
                    .border(BorderStroke(2.dp, androidx.compose.ui.graphics.Color.Black), shape = RoundedCornerShape(10.dp))
            ) {
                Row {
                    Text(text = stringResource(id = R.string.sell_free),
                        Modifier
                            .width(100.dp)
                        , FreeSellColor, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { /*TODO*/ }, Modifier.background(ButtonColor)) {
                Text(text = stringResource(id = R.string.sell_free),
                    Modifier
                        .width(100.dp)
                    , ButtonTextColor, fontSize = 20.sp)
            }
        }
        Image(painter = painterResource(id = R.drawable.with_cream), contentDescription = null,
            modifier = Modifier
                .height(166.dp)
                .width(166.dp)
                .align(Alignment.CenterVertically))
        Image(painter = painterResource(id = R.drawable.without_cream), contentDescription = null,
            modifier = Modifier
                .height(166.dp)
                .width(166.dp)
                .align(Alignment.CenterVertically))
    }
}