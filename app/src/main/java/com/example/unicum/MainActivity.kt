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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
            FragmentSettings(navController, navBackStackEntry.arguments?.getString("id") ?: "-1")
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
            .background(androidx.compose.ui.graphics.Color.Black)
            .padding(12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(250.dp),
            contentPadding = PaddingValues(25.dp, 25.dp),
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
                            painterResource(id = drink.img),
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
                                .fillMaxWidth(),
                            TextName,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .width(250.dp)
                                    .wrapContentHeight()
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
fun FragmentSettings(navController: NavController, id: String) {
    val viewModel = DrinksViewModel()
    val drinkState = viewModel.drink.collectAsState()
    viewModel.getDrinkById(id.toInt())
    val name = rememberSaveable { mutableStateOf(drinkState.value?.name) }
    val price = rememberSaveable { mutableStateOf(drinkState.value?.price) }
    val sellFree = rememberSaveable { mutableStateOf(drinkState.value?.free) }
    val img = rememberSaveable { mutableStateOf(drinkState.value?.img ?: R.drawable.with_cream) }

    val buttonColors = ButtonDefaults.buttonColors(ButtonColor)
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = BackField,
        errorContainerColor = BackField,
        disabledContainerColor = BackField,
        unfocusedContainerColor = BackField
    )
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = androidx.compose.ui.graphics.Color.White,
        checkedTrackColor = ButtonColor
    )
    Row(
        Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
    ) {
        Column(
            Modifier
                .width(418.dp)
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.naimenovanie),
                Modifier
                    .width(418.dp)
                    .padding(10.dp)
                    .align(Alignment.Start), TextName, fontSize = 16.sp
            )
            Box(
                Modifier
                    .width(418.dp)
                    .height(52.dp)
                    .background(BackField)
            ) {
                TextField(
                    value = name.value ?: "",
                    onValueChange = { newValue: String -> name.value = newValue },
                    modifier = Modifier
                        .width(418.dp)
                        .background(BackField),
                    enabled = true,
                    readOnly = false,
                    textStyle = TextStyle(color = WhiteTextName, fontSize = 20.sp),
                    colors = textFieldColors
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.price),
                Modifier
                    .width(418.dp)
                    .padding(10.dp)
                    .align(Alignment.Start), TextName, fontSize = 16.sp
            )
            Box(
                Modifier
                    .width(418.dp)
                    .height(52.dp)
                    .background(BackField)
            ) {
                Row {
                    TextField(
                        value = price.value ?: "",
                        onValueChange = { newValue: String -> price.value = newValue },
                        modifier = Modifier.background(BackField),
                        enabled = true,
                        readOnly = false,
                        textStyle = TextStyle(color = WhiteTextName, fontSize = 20.sp),
                        colors = textFieldColors
                    )
                    Text(
                        text = stringResource(id = R.string.rubble),
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.CenterVertically),
                        RubbleColor,
                        fontSize = 20.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .border(
                        BorderStroke(2.dp, androidx.compose.ui.graphics.Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.sell_free),
                        Modifier.padding(10.dp), FreeSellColor, fontSize = 14.sp
                    )
                    Switch(
                        checked = sellFree.value ?: false,
                        onCheckedChange = { flag -> sellFree.value = flag },
                        enabled = true,
                        colors = switchColors
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val drink = Drink(
                        id.toInt(),
                        name.value ?: "Кофе амаретто",
                        price.value ?: "199",
                        sellFree.value ?: false,
                        img.value,
                        drinkState.value?.volume ?: "0.33"
                    )
                    viewModel.updateDrink(drink)
                    navController.navigate("FragmentDrinks")
                },
                Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(10.dp), shape = RoundedCornerShape(12.dp), colors = buttonColors
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    Modifier, ButtonTextColor, fontSize = 20.sp
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.with_cream), contentDescription = null,
            modifier = Modifier
                .height(166.dp)
                .width(166.dp)
                .align(Alignment.CenterVertically)
                .clickable { img.value = R.drawable.with_cream }
        )
        Image(
            painter = painterResource(id = R.drawable.without_cream), contentDescription = null,
            modifier = Modifier
                .height(166.dp)
                .width(166.dp)
                .align(Alignment.CenterVertically)
                .clickable { img.value = R.drawable.without_cream }
        )
    }
}