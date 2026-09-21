package com.DipuoMokwena.pantrychef.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var theme by remember { mutableStateOf("system") }
    var message by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    displayName = doc.getString("displayName") ?: displayName
                    theme = doc.getString("theme") ?: "system"
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Logged in as: ${user?.email ?: "Unknown"}")
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Theme")
        Spacer(modifier = Modifier.height(8.dp))

        listOf("light", "dark", "system").forEach { option ->
            Row {
                RadioButton(
                    selected = theme == option,
                    onClick = { theme = option }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(message)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val uid = user?.uid
                if (uid == null) {
                    message = "You must be logged in"
                    return@Button
                }
                isSaving = true
                val data = hashMapOf(
                    "displayName" to displayName.trim(),
                    "theme" to theme,
                    "email" to (user.email ?: "")
                )
                db.collection("users").document(uid).set(data)
                    .addOnSuccessListener {
                        isSaving = false
                        message = "Settings saved"
                    }
                    .addOnFailureListener {
                        isSaving = false
                        message = it.localizedMessage ?: "Could not save settings"
                    }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Saving..." else "Save settings")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}