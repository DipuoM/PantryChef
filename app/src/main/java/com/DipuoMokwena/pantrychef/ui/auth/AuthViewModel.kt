package com.DipuoMokwena.pantrychef.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val resetEmailSent: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Email and password cannot be empty")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(error = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                _uiState.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState(error = "Please enter your email")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.sendPasswordResetEmail(email.trim()).await()
                _uiState.value = AuthUiState(resetEmailSent = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.localizedMessage ?: "Could not send reset email")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}