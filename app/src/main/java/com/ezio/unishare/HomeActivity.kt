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
import androidx.compose.ui.tooling.preview.Preview // Added this import
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ezio.unishare.ui.theme.PeerRentTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeerRentTheme {
                UniShareHomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniShareHomeScreen() {
    Scaffold(
        topBar = { CustomTopBar() },
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            item { SearchBarSection() }

            // Banner Carousel
            item { BannerCarousel() }

            // Categories
            item { CategoryRow() }

            // Section Title
            item {
                Text(
                    "Popular Rentals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Product Grid
            item { RentalItemGrid() }
        }
    }
}

@Composable
fun CustomTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Home", fontSize = 20.sp, style = MaterialTheme.typography.titleMedium)
            Text(" your House name, location...", fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(onClick = { /* TODO: Profile */ }) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.LightGray)
            )
        }
    }
}

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
                AsyncImage(model = banners[index], contentDescription = "Banner")
            }
        }
    }
}

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
                            .fillMaxWidth()
                    )
                    Text(name, style = MaterialTheme.typography.titleSmall)
                    Text(price, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { /*TODO*/ },
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = { Text("Rentals") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = { Text("Profile") },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PeerRentTheme {
        UniShareHomeScreen()
    }
}
