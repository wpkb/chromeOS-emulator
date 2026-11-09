package com.example.chromeos.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppId(val idStr: String, val displayName: String, val defaultTitle: String) {
  CHROME("chrome", "Chrome", "Google Chrome"),
  TERMINAL("terminal", "Terminal (Linux)", "crostini@penguin:~"),
  FILES("files", "Files", "Files - My files"),
  SETTINGS("settings", "Settings", "Settings"),
  TASK_MANAGER("task_manager", "Task Manager", "Task Manager - ChromeOS"),
  WEB_STORE("web_store", "Web Store", "Chrome Web Store"),
  CALCULATOR("calculator", "Calculator", "Calculator"),
  TEXT_EDITOR("text_editor", "Text", "Untitled Document - Text"),
  CANVAS("canvas", "Canvas", "Chrome Canvas"),
  DIAGNOSTICS("diagnostics", "Diagnostics", "System & Android 7+ Diagnostics"),
  CAMERA("camera", "Camera", "Camera")
}

enum class WindowSnapState {
  NONE,
  LEFT_HALF,
  RIGHT_HALF,
  MAXIMIZED
}

enum class ShelfPosition {
  BOTTOM,
  LEFT,
  RIGHT
}

data class ChromeOSWindow(
  val id: String,
  val appId: AppId,
  val title: String,
  val position: Offset = Offset(60f, 60f),
  val size: Pair<Dp, Dp> = Pair(420.dp, 360.dp),
  val isMinimized: Boolean = false,
  val snapState: WindowSnapState = WindowSnapState.NONE,
  val zIndex: Float = 1f,
  val extraPayload: String? = null
)

data class InstalledApp(
  val appId: AppId,
  val name: String,
  val isPinned: Boolean = true,
  val category: String = "Google",
  val description: String = ""
)

data class ChromeOSNotification(
  val id: String,
  val title: String,
  val message: String,
  val timestamp: String,
  val iconAppId: AppId? = null
)

enum class ChromeOSWallpaper(
  val title: String,
  val subtitle: String,
  val primaryGradient: List<Long>,
  val accentColor: Long
) {
  RADIANCE(
    title = "Radiance (Default)",
    subtitle = "Iconic ChromeOS Aura Gradient",
    primaryGradient = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364, 0xFF1A73E8),
    accentColor = 0xFF8AB4F8
  ),
  HERITAGE_DUNES(
    title = "Heritage Dunes",
    subtitle = "Warm desert dusk atmosphere",
    primaryGradient = listOf(0xFF2E112D, 0xFF540032, 0xFF820333, 0xFFC9283E, 0xFFF0433A),
    accentColor = 0xFFF0433A
  ),
  NOCTURNE(
    title = "Nocturne Dark",
    subtitle = "Deep midnight slate",
    primaryGradient = listOf(0xFF0A0C10, 0xFF161B22, 0xFF21262D, 0xFF30363D),
    accentColor = 0xFF58A6FF
  ),
  CHROMIUM_PRISM(
    title = "Chromium Prism",
    subtitle = "Classic Chrome emblem spectrum",
    primaryGradient = listOf(0xFF1E3C72, 0xFF2A5298, 0xFF1A73E8, 0xFF0D47A1),
    accentColor = 0xFFFBBC04
  ),
  CYBER_EMERALD(
    title = "Crostini Emerald",
    subtitle = "Linux Crostini terminal vibe",
    primaryGradient = listOf(0xFF051911, 0xFF0B2E1E, 0xFF14452F, 0xFF1E5E3F),
    accentColor = 0xFF34A853
  )
}
