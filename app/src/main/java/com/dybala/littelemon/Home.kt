package com.dybala.littelemon

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.room.Room
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dybala.littelemon.ui.theme.PrimaryGreen
import com.dybala.littelemon.ui.theme.PrimaryYellow
import com.dybala.littelemon.ui.theme.Secondary2
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun Home(navHostController: NavHostController) {

    val database: AppDatabase

    database = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java,
        "database"
    ).build()

    val databaseMenuItems = getAllDatabaseMenuItems(database).observeAsState(emptyList()).value

    val searchPhrase = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        if (isDatabaseEmpty(database)) {
            val list = fetchMenu()
            saveMenuToDatabaseInBackground(database, list)
        }
    }

    Column() {
        Header(navHostController)
        UpperPanel() {
            searchPhrase.value = it
        }
        LowerPanel(databaseMenuItems, searchPhrase)
    }
}

suspend fun isDatabaseEmpty(database: AppDatabase): Boolean {
    return withContext(Dispatchers.IO) {
        // Perform database query here
        database.menuItemDao().isEmpty()
    }
}

fun getAllDatabaseMenuItems(database: AppDatabase): LiveData<List<MenuItemRoom>> {
    return database.menuItemDao().getAll()
}

private suspend fun fetchMenu(): List<MenuItemNetwork> {
    val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }
    val httpResponse: MenuNetwork =
        httpClient.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/menu.json")
            .body()
    return httpResponse.items
}

private fun saveMenuToDatabase(database: AppDatabase, menuItemsNetwork: List<MenuItemNetwork>) {
    val menuItemsRoom = menuItemsNetwork.map { it.toMenuItemRoom() }
    database.menuItemDao().insertAll(*menuItemsRoom.toTypedArray())
}

suspend fun saveMenuToDatabaseInBackground(
    database: AppDatabase,
    menuItemsNetwork: List<MenuItemNetwork>
) {
    withContext(Dispatchers.IO) {
        val menuItemsRoom = menuItemsNetwork.map { it.toMenuItemRoom() }
        database.menuItemDao().insertAll(*menuItemsRoom.toTypedArray())
    }
}


@Composable
fun Header(navController: NavHostController) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(50.dp))
        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = "Little Lemon Logo",
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(50.dp)
        )

        Box(modifier = Modifier
            .size(50.dp)
            .clickable { navController.navigate(Profile.route) }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                tint = PrimaryGreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 2.dp)
            )
        }


    }
}


@Composable
fun UpperPanel(search: (parameter: String) -> Unit) {
    val searchPhrase = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .background(PrimaryGreen)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = "Little Lemon", style = MaterialTheme.typography.h1, color = PrimaryYellow)
        Text(text = "New York", style = MaterialTheme.typography.h3, color = Color.White)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "We are a family owned Mediterranean restaurant, focused on traditional recipes served with  a modern twist. Turkish, Italian, Indian and chinese recipes are our speciality.",
                modifier = Modifier.fillMaxWidth(0.7f),
                color = Color.White,
                style = MaterialTheme.typography.body1
            )
            Image(
                painter = painterResource(id = R.drawable.hero_image),
                contentDescription = "Hero Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(
            value = searchPhrase.value,
            onValueChange = {
                searchPhrase.value = it
                search(searchPhrase.value)
            },
            placeholder = {
                Text(text = "Enter Search Phrase")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                textColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

    }

}

@Composable
fun LowerPanel(databaseMenuItems: List<MenuItemRoom>, search: MutableState<String>) {
    val categories = databaseMenuItems.map {
        it.category.replaceFirstChar { character ->
            character.uppercase()
        }
    }.toSet()


    val selectedCategory = remember {
        mutableStateOf("")
    }


    val items = if (search.value == "") {
        databaseMenuItems

    } else {
        databaseMenuItems.filter {
            it.title.contains(search.value, ignoreCase = true)

        }


    }


    val filteredItems = if (selectedCategory.value == "" || selectedCategory.value == "all") {
        items
    } else {
        items.filter {
            it.category.contains(selectedCategory.value, ignoreCase = true)
        }
    }


    Column {
        MenuCategories(categories) {
            selectedCategory.value = it
        }
        Spacer(modifier = Modifier.size(2.dp))
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            for (item in filteredItems) {
                MenuItem(item = item)
            }
        }

    }
}


@Composable
fun MenuCategories(categories: Set<String>, categoryLambda: (sel: String) -> Unit) {
    val cat = remember {
        mutableStateOf("")
    }

    Card(elevation = 10.dp, modifier = Modifier.fillMaxWidth()) {

        Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Text(text = "ORDER FOR DELIVERY", fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                CategoryButton(category = "All") {
                    cat.value = it.lowercase()
                    categoryLambda(it.lowercase())
                }

                for (category in categories) {
                    CategoryButton(category = category) {
                        cat.value = it
                        categoryLambda(it)
                    }

                }

            }
        }
    }
}

@Composable
fun CategoryButton(category: String, selectedCategory: (sel: String) -> Unit) {
    val isClicked = remember {
        mutableStateOf(false)
    }
    Button(
        onClick = {
            isClicked.value = !isClicked.value
            selectedCategory(category)

        },
        colors = ButtonDefaults.buttonColors(
            contentColor = PrimaryGreen,
            backgroundColor = Secondary2
        )
    ) {
        Text(text = category)
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun MenuItem(item: MenuItemRoom) {

    val itemDescription = if (item.description.length > 100) {
        item.description.substring(0, 100) + ". . ."
    } else {
        item.description
    }

    Card(elevation = 4.dp,
        modifier = Modifier
            .clickable {

            }) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.fillMaxWidth(0.7f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(text = itemDescription, modifier = Modifier.padding(bottom = 10.dp))
                Text(text = "$ ${item.price}", fontWeight = FontWeight.Bold)

            }

            GlideImage(
                model = item.imageUrl,
                contentDescription = "",
                Modifier.size(100.dp, 100.dp),
                contentScale = ContentScale.Crop
            )
        }
    }

}