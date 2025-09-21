package com.ezio.unishare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ezio.unishare.ui.theme.PeerRentTheme

// ------------------ SCREENS ENUM ------------------
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Rentals : Screen("rentals")
    object Profile : Screen("profile")
}

// ------------------ MAIN ACTIVITY ------------------
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeerRentTheme {
                UniShareApp()
            }
        }
    }
}

// ------------------ APP WITH NAVIGATION ------------------
@Composable
fun UniShareApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) { UniShareHomeScreen() }
            composable(Screen.Rentals.route) { RentalScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}

// ------------------ HOME SCREEN ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniShareHomeScreen() {
    Scaffold(
        topBar = { CustomTopBar() }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { SearchBarSection() }
            item { BannerCarousel() }
            item { CategoryRow() }
            item {
                Text(
                    "Popular Rentals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item { RentalItemGrid() }
        }
    }
}

// ------------------ TOP BAR ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar() {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Home", fontSize = 20.sp, style = MaterialTheme.typography.titleMedium)
                    Text("Your house name, location...", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Profile */ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// ------------------ SEARCH BAR ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarSection() {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        placeholder = { Text("Search for rentals...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        trailingIcon = { Icon(Icons.Filled.Mic, contentDescription = "Mic") }
    )
}

// ------------------ BANNER CAROUSEL ------------------
@Composable
fun BannerCarousel() {
    val banners = listOf(
        "https://picsum.photos/600/200?1",
        "https://picsum.photos/600/200?2",
        "https://picsum.photos/600/200?3"
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(banners.size) { index ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(150.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                AsyncImage(
                    model = banners[index],
                    contentDescription = "Banner Image ${index + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// ------------------ CATEGORIES ------------------
@Composable
fun CategoryRow() {
    val categories = listOf("Books", "Electronics", "Furniture", "Sports", "Notes", "Other")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories.size) { index ->
            AssistChip(
                onClick = { /* TODO */ },
                label = { Text(categories[index]) }
            )
        }
    }
}

// ------------------ RENTAL GRID ------------------
@Composable
fun RentalItemGrid() {
    val items = listOf(
        "Camera" to "₹200/day",
        "Laptop" to "₹500/day",
        "Bike" to "₹100/day",
        "Projector" to "₹300/day"
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(400.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { (name, price) ->
            Card {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = "https://picsum.photos/200?random=$name",
                        contentDescription = name,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Text(name, style = MaterialTheme.typography.titleSmall)
                    Text(price, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ------------------ RENTALS PAGE ------------------
@Composable
fun RentalScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Rentals", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("Currently Rented Items", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(3) { index ->
                Text("Rented Item ${index + 1}", modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Recommended for You", style = MaterialTheme.typography.titleMedium)
        LazyRow {
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Item $index")
                    }
                }
            }
        }
    }
}

// ------------------ PROFILE PAGE ------------------
@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Page (Coming Soon)")
    }
}

// ------------------ BOTTOM NAVIGATION ------------------
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Home.route) },
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Rentals.route) },
            label = { Text("Rentals") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Profile.route) },
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )
    }
}

// ------------------ PREVIEW ------------------
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PeerRentTheme {
        UniShareApp()
    }
}
