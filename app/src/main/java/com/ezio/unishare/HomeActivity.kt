package com.ezio.unishare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ezio.unishare.ui.theme.PeerRentTheme
import kotlinx.coroutines.delay
import kotlin.math.*

// ------------------ MAIN ACTIVITY ------------------
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeerRentTheme {
                UniShareAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniShareAppScreen() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == Screen.Home.route || currentRoute == Screen.Rentals.route) {
                CustomTopBar(scrollBehavior, currentRoute, navController)
            }
        },
        bottomBar = { SmoothGooeyBottomNav(navController = navController) }
    ) { paddingValues ->
        AppNavHost(navController, Modifier.padding(paddingValues))
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreenContent() }
        composable(Screen.Rentals.route) { RentalScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

@Composable
fun HomeScreenContent() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
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
        items(5) { index ->
            Text("More item $index", modifier = Modifier.padding(16.dp))
        }
    }
}

// ------------------ RENTAL SCREEN ------------------
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
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(3) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        "Rented Item ${index + 1}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Recommended for You", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Item $index")
                    }
                }
            }
        }
    }
}

// ------------------ PROFILE SCREEN ------------------
@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Page (Coming Soon)", style = MaterialTheme.typography.headlineMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(scrollBehavior: TopAppBarScrollBehavior?, currentRoute: String?, navController: NavHostController) {
    val title = when (currentRoute) {
        Screen.Home.route -> "Home"
        Screen.Rentals.route -> "Rentals"
        else -> ""
    }
    val subTitle = when (currentRoute) {
        Screen.Home.route -> "your House name, location..."
        else -> null
    }

    TopAppBar(
        title = {
            Column {
                Text(title, fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
                if (subTitle != null) {
                    Text(subTitle, fontSize = 12.sp, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        actions = {
            if (currentRoute == Screen.Home.route) {
                IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
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
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
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
        items(banners) { bannerUrl ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(150.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = "Banner Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
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
        items(categories) { category ->
            AssistChip(onClick = { }, label = { Text(category) })
        }
    }
}

@Composable
fun RentalItemGrid() {
    val itemsData = listOf(
        "Camera" to "₹200/day", "Laptop" to "₹500/day",
        "Bike" to "₹100/day", "Projector" to "₹300/day"
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(280.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemsData) { (name, price) ->
            Card {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
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

// ----------------- NAVIGATION BAR DATA -----------------
data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// ----------------- SMOOTH PARTICLE EFFECT -----------------
@Composable
fun SmoothParticleEffect(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    repeat(6) { i ->
        val angle = (360f / 6f) * i
        val delay = i * 50

        val distance by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = if (isActive) 30f else 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, delayMillis = delay, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "distance$i"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = if (isActive) 0.8f else 0f,
            targetValue = if (isActive) 0f else 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha$i"
        )

        if (isActive) {
            Box(
                modifier = Modifier
                    .offset(
                        x = (cos(angle * PI / 180f) * distance).dp,
                        y = (sin(angle * PI / 180f) * distance).dp
                    )
                    .size(6.dp)
                    .background(
                        Color(0xFF0000FF).copy(alpha = alpha),  // ← Changed from Color.White to Color(0xFF0000FF)
                        CircleShape
                    )
            )
        }
    }
}

// ----------------- SMOOTH GOOEY NAVIGATION -----------------
@Composable
fun SmoothGooeyBottomNav(navController: NavHostController) {
    val haptic = LocalHapticFeedback.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedIndex by remember { mutableIntStateOf(0) }

    // Smooth animated offset with more fluid motion
    val animatedOffset by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "gooeyOffset"
    )

    val navigationItems = listOf(
        NavigationItem(Screen.Home.route, Screen.Home.title, Screen.Home.icon),
        NavigationItem(Screen.Rentals.route, Screen.Rentals.title, Screen.Rentals.icon),
        NavigationItem(Screen.Profile.route, Screen.Profile.title, Screen.Profile.icon)
    )

    // Update selected index when route changes
    LaunchedEffect(currentRoute) {
        val newIndex = navigationItems.indexOfFirst { it.route == currentRoute }
        if (newIndex != -1) {
            selectedIndex = newIndex
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        // Enhanced Canvas with cleaner blob (removed connecting circles)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val itemWidth = size.width / navigationItems.size
            val blobCenterX = itemWidth * (animatedOffset + 0.5f)
            val blobCenterY = size.height / 2f

            // Main pill-shaped background
            drawPillShape(
                center = Offset(blobCenterX, blobCenterY - 8.dp.toPx()),
                width = 80.dp.toPx(),
                height = 50.dp.toPx(),
                color = Color(0xFF1E90FF)
            )
        }

        // Navigation items with enhanced animations
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEachIndexed { index, navItem ->
                val isSelected = selectedIndex == index

                // Smooth scale animation
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium              // ← Reverted back to original
                    ),
                    label = "iconScale$index"
                )

                // Color animation
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Gray,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),    // ← Reverted back to 300ms
                    label = "iconColor$index"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (selectedIndex != index) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navController.navigate(navItem.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        .scale(scale)
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            navItem.icon,
                            contentDescription = navItem.label,
                            tint = if (isSelected) Color(0xFF0000FF) else Color.Gray,  // ← Direct color assignment to ensure it works
                            modifier = Modifier.size(if (isSelected) 26.dp else 22.dp)
                        )

                        // Enhanced particle effect
                        SmoothParticleEffect(isSelected)
                    }

                    // Animated label visibility
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(300)) + slideInVertically(        // ← Reverted back to 300ms
                            tween(300),                                        // ← Reverted back to 300ms
                            initialOffsetY = { it / 2 }
                        ),
                        exit = fadeOut(tween(200)) + slideOutVertically(       // ← Reverted back to 200ms
                            tween(200),                                        // ← Reverted back to 200ms
                            targetOffsetY = { it / 2 }
                        )
                    ) {
                        Text(
                            text = navItem.label,
                            fontSize = 11.sp,
                            color = if (isSelected) Color(0xFF0000FF) else Color.Gray,  // ← Also apply same logic to text
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to draw simple pill shape
fun DrawScope.drawPillShape(
    center: Offset,
    width: Float,
    height: Float,
    color: Color
) {
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.9f),
                color.copy(alpha = 0.7f)
            ),
            center = center,
            radius = width / 2f
        ),
        topLeft = Offset(
            center.x - width / 2f,
            center.y - height / 2f
        ),
        size = Size(width, height),
        cornerRadius = CornerRadius(height / 2f, height / 2f) // Makes it perfectly pill-shaped
    )
}

// Keep the old smooth blob function as backup
fun DrawScope.drawSmoothBlob(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path().apply {
        // Create organic blob shape with bezier curves
        val points = 8
        val angleStep = 2 * PI / points

        for (i in 0 until points) {
            val angle = i * angleStep
            val nextAngle = ((i + 1) % points) * angleStep

            val radiusVariation = radius * (0.9f + 0.2f * sin(i * 0.7f).toFloat())
            val nextRadiusVariation = radius * (0.9f + 0.2f * sin((i + 1) * 0.7f).toFloat())

            val x = center.x + radiusVariation * cos(angle).toFloat()
            val y = center.y + radiusVariation * sin(angle).toFloat()

            val nextX = center.x + nextRadiusVariation * cos(nextAngle).toFloat()
            val nextY = center.y + nextRadiusVariation * sin(nextAngle).toFloat()

            if (i == 0) {
                moveTo(x, y)
            }

            // Control points for smooth curves
            val controlAngle = angle + angleStep / 2
            val controlRadius = radius * 1.1f
            val controlX = center.x + controlRadius * cos(controlAngle).toFloat()
            val controlY = center.y + controlRadius * sin(controlAngle).toFloat()

            quadraticBezierTo(controlX, controlY, nextX, nextY)
        }
        close()
    }

    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.95f),
                Color(0xFF1E90FF).copy(alpha = 0.6f)  // ← Updated gradient to use the same blue
            ),
            center = center,
            radius = radius
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PeerRentTheme {
        UniShareAppScreen()
    }
}