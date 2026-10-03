package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.CashPayDatabase
import com.example.data.remote.ApiClient
import com.example.data.repository.CashPayRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InstallationGuideScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OtpScreen
import com.example.ui.screens.PinScreen
import com.example.ui.screens.ProfileInstallScreen
import com.example.ui.screens.RegisterAccountScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthStep
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AuthViewModelFactory
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.DashboardViewModelFactory
import kotlinx.coroutines.launch

enum class AppStartupState {
    SPLASH,
    ONBOARDING,
    APP
}

class MainActivity : ComponentActivity() {

    private val database by lazy { CashPayDatabase.getDatabase(this) }
    private val repository by lazy { CashPayRepository(ApiClient.apiService, database.cashPayDao()) }

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(repository)
    }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        val prefs = getSharedPreferences("cashpay_preferences", Context.MODE_PRIVATE)

        lifecycleScope.launch {
            database.cashPayDao().getSessionOnce()?.let { session ->
                ApiClient.sessionToken = session.sessionToken
            }
        }

        setContent {
            val dashboardState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
            var startupState by remember { mutableStateOf(AppStartupState.SPLASH) }

            MyApplicationTheme(darkTheme = dashboardState.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0) // Full screen edge-to-edge
                ) { _ ->
                    when (startupState) {
                        AppStartupState.SPLASH -> {
                            SplashScreen(
                                onAnimationFinished = {
                                    val hasSeenOnboarding = prefs.getBoolean("has_seen_onboarding_v4", false)
                                    startupState = if (!hasSeenOnboarding) {
                                        AppStartupState.ONBOARDING
                                    } else {
                                        AppStartupState.APP
                                    }
                                }
                            )
                        }
                        AppStartupState.ONBOARDING -> {
                            InstallationGuideScreen(
                                onFinished = {
                                    prefs.edit().putBoolean("has_seen_onboarding_v4", true).apply()
                                    startupState = AppStartupState.APP
                                }
                            )
                        }
                        AppStartupState.APP -> {
                            CashPayAppRoot(
                                authViewModel = authViewModel,
                                dashboardViewModel = dashboardViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Lock session to PIN / Biometrics when app is put to background for bank-grade security
        lifecycleScope.launch {
            repository.lockSession()
        }
    }
}

@Composable
fun CashPayAppRoot(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val session by authViewModel.currentSession.collectAsStateWithLifecycle()

    // STRICT SECURITY: The Dashboard is NEVER accessible without valid PIN confirmation (AuthStep.COMPLETED)
    val isAuthenticated = authState.authStep == AuthStep.COMPLETED

    if (isAuthenticated) {
        DashboardScreen(
            authViewModel = authViewModel,
            dashboardViewModel = dashboardViewModel,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Handle Android system back button
        if (authState.authStep != AuthStep.IDENTIFICATION) {
            BackHandler {
                authViewModel.navigateBack()
            }
        }

        AnimatedContent(
            targetState = authState.authStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "auth_screen_transition",
            modifier = modifier.fillMaxSize()
        ) { step ->
            when (step) {
                AuthStep.IDENTIFICATION -> {
                    LoginScreen(
                        viewModel = authViewModel,
                        uiState = authState
                    )
                }
                AuthStep.REGISTER_FORM, AuthStep.REGISTER_SUCCESS -> {
                    RegisterAccountScreen(
                        viewModel = authViewModel,
                        uiState = authState
                    )
                }
                AuthStep.OTP_CHANNEL_SELECT, AuthStep.OTP_VERIFICATION -> {
                    OtpScreen(
                        viewModel = authViewModel,
                        uiState = authState
                    )
                }
                AuthStep.PIN_ENTRY -> {
                    PinScreen(
                        viewModel = authViewModel,
                        uiState = authState
                    )
                }
                AuthStep.PROFILE_INSTALLATION -> {
                    ProfileInstallScreen(
                        viewModel = authViewModel,
                        uiState = authState
                    )
                }
                AuthStep.COMPLETED -> {
                    DashboardScreen(
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
