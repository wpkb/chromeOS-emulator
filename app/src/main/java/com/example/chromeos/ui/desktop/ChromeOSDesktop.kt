package com.example.chromeos.ui.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.model.ShelfPosition
import com.example.chromeos.ui.components.ChromeOSAppIcon
import com.example.chromeos.ui.launcher.ChromeOSLauncher
import com.example.chromeos.ui.quicksettings.ChromeOSQuickSettings
import com.example.chromeos.ui.shelf.ChromeOSShelf
import com.example.chromeos.ui.window.AuraWindow
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue

@Composable
fun ChromeOSDesktop(
  viewModel: ChromeOSViewModel
) {
  val isDarkMode by viewModel.isDarkMode.collectAsState()
  val isNightLight by viewModel.isNightLightOn.collectAsState()
  val wallpaper by viewModel.currentWallpaper.collectAsState()
  val windows by viewModel.windows.collectAsState()
  val shelfPos by viewModel.shelfPosition.collectAsState()
  val isLauncherOpen by viewModel.isLauncherOpen.collectAsState()
  val isQuickSettingsOpen by viewModel.isQuickSettingsOpen.collectAsState()
  val displayScaling by viewModel.displayScaling.collectAsState()

  var showDesktopContextMenu by remember { mutableStateOf(false) }
  var contextMenuOffset by remember { mutableStateOf(Offset.Zero) }

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Aura Wallpaper Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val gradientColors = wallpaper.primaryGradient.map { Color(it) }
      drawRect(
        brush = Brush.radialGradient(
          colors = gradientColors,
          center = Offset(size.width * 0.5f, size.height * 0.4f),
          radius = size.maxDimension * 0.85f
        )
      )
    }

    // 2. Desktop Tap & Long-Press Handler
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures(
            onTap = {
              showDesktopContextMenu = false
              viewModel.closeLauncher()
              viewModel.closeQuickSettings()
            },
            onLongPress = { offset ->
              contextMenuOffset = offset
              showDesktopContextMenu = true
            }
          )
        }
    )

    // 3. Desktop Layout (Workspace + Shelf)
    Row(modifier = Modifier.fillMaxSize()) {
      if (shelfPos == ShelfPosition.LEFT) {
        ChromeOSShelf(viewModel = viewModel, isDarkMode = isDarkMode)
      }

      Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
        // Desktop Workspace (Window Container)
        BoxWithConstraints(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          val containerWidth = maxWidth.value
          val containerHeight = maxHeight.value

          // Desktop Shortcuts Grid (Top-Left)
          Column(
            modifier = Modifier
              .padding(16.dp)
              .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            listOf(
              AppId.CHROME,
              AppId.FILES,
              AppId.TERMINAL,
              AppId.WEB_STORE,
              AppId.SETTINGS,
              AppId.DIAGNOSTICS
            ).forEach { appId ->
              val app = viewModel.allApps.find { it.appId == appId } ?: return@forEach
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .width(68.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { viewModel.openApp(appId) }
                  .padding(vertical = 4.dp)
              ) {
                ChromeOSAppIcon(appId = appId, size = 38.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = app.name,
                  fontSize = 10.sp,
                  color = Color.White,
                  fontWeight = FontWeight.Medium,
                  textAlign = TextAlign.Center,
                  maxLines = 1
                )
              }
            }
          }

          // Active Windows Layer
          windows.forEach { win ->
            AuraWindow(
              window = win,
              viewModel = viewModel,
              containerWidthDp = containerWidth,
              containerHeightDp = containerHeight,
              isDarkMode = isDarkMode
            )
          }
        }

        // Bottom Shelf
        if (shelfPos == ShelfPosition.BOTTOM) {
          ChromeOSShelf(viewModel = viewModel, isDarkMode = isDarkMode)
        }
      }

      if (shelfPos == ShelfPosition.RIGHT) {
        ChromeOSShelf(viewModel = viewModel, isDarkMode = isDarkMode)
      }
    }

    // 4. Desktop Context Menu
    if (showDesktopContextMenu) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable { showDesktopContextMenu = false }
      ) {
        Card(
          modifier = Modifier
            .offset(contextMenuOffset.x.dp.coerceAtMost(200.dp), contextMenuOffset.y.dp.coerceAtMost(300.dp))
            .width(220.dp),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xF0202124) else Color(0xF5FFFFFF)
          )
        ) {
          Column(modifier = Modifier.padding(6.dp)) {
            DropdownMenuItem(
              text = { Text("Set wallpaper...", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp)) },
              onClick = {
                showDesktopContextMenu = false
                viewModel.openApp(AppId.SETTINGS)
              }
            )
            DropdownMenuItem(
              text = { Text("Open Linux Terminal", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp)) },
              onClick = {
                showDesktopContextMenu = false
                viewModel.openApp(AppId.TERMINAL)
              }
            )
            DropdownMenuItem(
              text = { Text("New text file", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp)) },
              onClick = {
                showDesktopContextMenu = false
                viewModel.openApp(AppId.TEXT_EDITOR)
              }
            )
            DropdownMenuItem(
              text = { Text("Autohide shelf", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) },
              onClick = {
                showDesktopContextMenu = false
                viewModel.toggleShelfAutoHide()
              }
            )
            DropdownMenuItem(
              text = { Text("About ChromeOS Emulator", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp)) },
              onClick = {
                showDesktopContextMenu = false
                viewModel.openApp(AppId.SETTINGS)
              }
            )
          }
        }
      }
    }

    // 5. Launcher Overlay
    if (isLauncherOpen) {
      ChromeOSLauncher(viewModel = viewModel, isDarkMode = isDarkMode)
    }

    // 6. Quick Settings Overlay
    if (isQuickSettingsOpen) {
      ChromeOSQuickSettings(viewModel = viewModel, isDarkMode = isDarkMode)
    }

    // 7. Night Light warm screen filter
    if (isNightLight) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x22FF9800))
      )
    }
  }
}
