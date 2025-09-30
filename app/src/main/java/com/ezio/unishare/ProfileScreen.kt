package com.ezio.unishare

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Data class to hold user information from Firebase
data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val collegeMail: String = "",
    val phone: String = ""
)

// A state holder for our data fetching
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, userEmail: String) {
    var profileUiState by remember { mutableStateOf<ProfileUiState>(ProfileUiState.Loading) }

    // This effect runs once to fetch the user's data from Firebase
    LaunchedEffect(key1 = userEmail) {
        if (userEmail.isBlank()) {
            profileUiState = ProfileUiState.Error("User email not provided.")
            return@LaunchedEffect
        }
        
        val database = FirebaseDatabase.getInstance()
        val userKey = userEmail.replace(".", "_")
        val userRef = database.getReference("users").child(userKey)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserProfile::class.java)
                profileUiState = if (user != null) {
                    ProfileUiState.Success(user)
                } else {
                    ProfileUiState.Error("User data not found for email: $userEmail")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                profileUiState = ProfileUiState.Error(error.message)
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Profile") })
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = profileUiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProfileUiState.Error -> {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
                is ProfileUiState.Success -> {
                    ProfileContent(user = state.user)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(user: UserProfile) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with initials
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val initials = (user.firstName.firstOrNull()?.toString() ?: "") + (user.lastName.firstOrNull()?.toString() ?: "")
            Text(
                text = initials.uppercase(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Full Name
        Text(
            text = "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // User Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileInfoRow(icon = Icons.Default.Person, label = "Full Name", value = "${user.firstName} ${user.lastName}")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(icon = Icons.Default.Email, label = "College Email", value = user.collegeMail)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone Number", value = user.phone)
            }
        }
        Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

        // --- UPDATED LOGOUT BUTTON ---
        Button(
            onClick = {
                // 1. Sign out from Firebase Authentication to end the session
                FirebaseAuth.getInstance().signOut()
                
                // 2. Navigate back to the login screen and clear all previous screens
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

