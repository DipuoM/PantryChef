package com.DipuoMokwena.pantrychef

import com.DipuoMokwena.pantrychef.ui.SearchScreen
import com.DipuoMokwena.pantrychef.ui.settings.SettingsScreen
import com.DipuoMokwena.pantrychef.ui.auth.LoginScreen
import com.DipuoMokwena.pantrychef.ui.auth.RegisterScreen
import com.DipuoMokwena.pantrychef.ui.auth.ForgotPasswordScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.DipuoMokwena.pantrychef.ui.DetailScreen
import com.DipuoMokwena.pantrychef.ui.auth.ForgotPasswordScreen
import com.DipuoMokwena.pantrychef.ui.auth.LoginScreen
import com.DipuoMokwena.pantrychef.ui.auth.RegisterScreen
import com.DipuoMokwena.pantrychef.ui.settings.SettingsScreen
import com.DipuoMokwena.pantrychef.ui.theme.PantryChefTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PantryChefTheme {
                PantryChefApp()
            }
        }
    }
}

private enum class Screen {
    Login, Register, ForgotPassword, Home, Settings, Search, Detail
}

@Composable
fun PantryChefApp() {
    val auth = FirebaseAuth.getInstance()
    var currentScreen by remember {
        mutableStateOf(
            if (auth.currentUser != null) Screen.Home else Screen.Login
        )
    }
    var selectedMealId by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onGoToRegister = { currentScreen = Screen.Register },
            onGoToForgotPassword = { currentScreen = Screen.ForgotPassword }
        )

        Screen.Register -> RegisterScreen(
            onRegisterSuccess = { currentScreen = Screen.Home },
            onGoToLogin = { currentScreen = Screen.Login }
        )

        Screen.ForgotPassword -> ForgotPasswordScreen(
            onBackToLogin = { currentScreen = Screen.Login }
        )

        Screen.Home -> HomeScreen(
            email = auth.currentUser?.email ?: "User",
            onOpenSettings = { currentScreen = Screen.Settings },
            onOpenSearch = { currentScreen = Screen.Search },
            onLogout = {
                auth.signOut()
                currentScreen = Screen.Login
            }
        )

        Screen.Settings -> SettingsScreen(
            onBack = { currentScreen = Screen.Home },
            onLogout = {
                auth.signOut()
                currentScreen = Screen.Login
            }
        )

        Screen.Search -> SearchScreen(
            onBack = { currentScreen = Screen.Home },
            onMealClick = { id ->
                selectedMealId = id
                currentScreen = Screen.Detail
            }
        )

        Screen.Detail -> DetailScreen(
            mealId = selectedMealId,
            onBack = { currentScreen = Screen.Search }
        )
    }
}

@Composable
fun HomeScreen(
    email: String,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to PantryChef", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Logged in as: $email")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onOpenSearch, modifier = Modifier.fillMaxWidth()) {
            Text("Search Recipes")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}