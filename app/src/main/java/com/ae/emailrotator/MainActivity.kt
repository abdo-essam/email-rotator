package com.ae.emailrotator

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.presentation.navigation.MainNavGraph
import com.ae.emailrotator.presentation.theme.EmailRotatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val isDark by settingsRepository.isDarkMode()
                .collectAsStateWithLifecycle(initialValue = false)

            EmailRotatorTheme(darkTheme = isDark) {
                MainNavGraph(
                    isDarkMode = isDark,
                    onToggleDarkMode = {
                        CoroutineScope(Dispatchers.IO).launch {
                            settingsRepository.setDarkMode(!isDark)
                        }
                    }
                )
            }
        }
    }
}