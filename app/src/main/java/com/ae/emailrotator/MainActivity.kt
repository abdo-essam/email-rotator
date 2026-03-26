package com.ae.emailrotator

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.presentation.navigation.MainNavGraph
import com.ae.emailrotator.presentation.theme.EmailRotatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var isDarkMode: Boolean? by mutableStateOf(null)
    private var isReady by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible until theme is loaded
        splashScreen.setKeepOnScreenCondition { !isReady }

        // Load theme preference before showing UI
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Get initial value first
                isDarkMode = settingsRepository.isDarkMode().first()
                isReady = true

                // Then observe changes
                settingsRepository.isDarkMode().collect { dark ->
                    isDarkMode = dark
                }
            }
        }

        requestNotificationPermissionIfNeeded()

        setContent {
            EmailRotatorTheme(darkTheme = isDarkMode) {
                if (isReady) {
                    MainNavGraph(
                        isDarkMode = isDarkMode ?: false,
                        onToggleDarkMode = {
                            lifecycleScope.launch {
                                settingsRepository.setDarkMode(!(isDarkMode ?: false))
                            }
                        }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}