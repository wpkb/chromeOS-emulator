package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chromeos.model.AppId
import com.example.chromeos.ui.desktop.ChromeOSDesktop
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val viewModel: ChromeOSViewModel = viewModel()

      LaunchedEffect(Unit) {
        viewModel.initHostInfo(this@MainActivity)
        // Automatically open Chrome Browser and Settings/About on first boot
        viewModel.openApp(AppId.CHROME)
      }

      val isDarkMode by viewModel.isDarkMode.collectAsState()
      val isLauncherOpen by viewModel.isLauncherOpen.collectAsState()
      val isQuickSettingsOpen by viewModel.isQuickSettingsOpen.collectAsState()
      val windows by viewModel.windows.collectAsState()

      // Back navigation handling for ChromeOS desktop
      BackHandler(enabled = isLauncherOpen || isQuickSettingsOpen || windows.any { !it.isMinimized }) {
        when {
          isLauncherOpen -> viewModel.closeLauncher()
          isQuickSettingsOpen -> viewModel.closeQuickSettings()
          windows.any { !it.isMinimized } -> {
            val topWindow = windows.filter { !it.isMinimized }.maxByOrNull { it.zIndex }
            if (topWindow != null) {
              viewModel.minimizeWindow(topWindow.id)
            }
          }
        }
      }

      MyApplicationTheme(darkTheme = isDarkMode) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = ChromeOSDarkBg
        ) {
          ChromeOSDesktop(viewModel = viewModel)
        }
      }
    }
  }
}
