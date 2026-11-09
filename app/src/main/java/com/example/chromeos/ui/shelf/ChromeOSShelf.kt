package com.example.chromeos.ui.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.model.ShelfPosition
import com.example.chromeos.ui.components.ChromeOSAppIcon
import com.example.chromeos.ui.components.ChromeOSLauncherButton
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSShelfDark
import com.example.ui.theme.ChromeOSShelfLight
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChromeOSShelf(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val isLauncherOpen by viewModel.isLauncherOpen.collectAsState()
  val isQuickSettingsOpen by viewModel.isQuickSettingsOpen.collectAsState()
  val windows by viewModel.windows.collectAsState()
  val shelfPos by viewModel.shelfPosition.collectAsState()
  val isWifi by viewModel.isWifiOn.collectAsState()
  val hostInfo by viewModel.hostInfo.collectAsState()
  val notifications by viewModel.notifications.collectAsState()

  var currentTime by remember {
    mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
  }

  // Periodic clock update
  LaunchedEffect(Unit) {
    while (true) {
      kotlinx.coroutines.delay(10000)
      currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
  }

  val shelfBg = if (isDarkMode) ChromeOSShelfDark else ChromeOSShelfLight

  when (shelfPos) {
    ShelfPosition.BOTTOM -> {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .background(shelfBg)
          .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Left: Launcher Button
        ChromeOSLauncherButton(
          isOpen = isLauncherOpen,
          size = 40.dp,
          modifier = Modifier.clickable { viewModel.toggleLauncher() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Center: App Icons (Pinned + Running)
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically
        ) {
          viewModel.allApps.filter { it.isPinned }.forEach { app ->
            val runningWin = windows.find { it.appId == app.appId }
            val isRunning = runningWin != null
            val isFocused = isRunning && !runningWin!!.isMinimized

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .padding(horizontal = 6.dp)
                .clickable {
                  if (isRunning) {
                    if (runningWin!!.isMinimized) {
                      viewModel.bringToFront(runningWin.id)
                    } else {
                      viewModel.minimizeWindow(runningWin.id)
                    }
                  } else {
                    viewModel.openApp(app.appId)
                  }
                }
            ) {
              ChromeOSAppIcon(appId = app.appId, size = 32.dp)
              Spacer(modifier = Modifier.height(2.dp))
              // Running dot indicator
              Box(
                modifier = Modifier
                  .size(if (isFocused) 6.dp else 4.dp, 3.dp)
                  .clip(CircleShape)
                  .background(
                    when {
                      isFocused -> ChromeOSBlue
                      isRunning -> Color.LightGray
                      else -> Color.Transparent
                    }
                  )
              )
            }
          }
        }

        // Right: Status Area / Quick Settings
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isQuickSettingsOpen) ChromeOSBlue.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f))
            .clickable { viewModel.toggleQuickSettings() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Wi-Fi icon
          Icon(
            imageVector = if (isWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = "Wi-Fi",
            tint = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.size(16.dp)
          )

          // Battery icon & percentage
          val batteryPct = hostInfo?.batteryPct ?: 88
          val isCharging = hostInfo?.isCharging ?: false
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
              contentDescription = "Battery",
              tint = if (isDarkMode) Color.White else Color.Black,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "$batteryPct%",
              fontSize = 11.sp,
              color = if (isDarkMode) Color.White else Color.Black
            )
          }

          // Digital clock
          Text(
            text = currentTime,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkMode) Color.White else Color.Black
          )

          // Notification indicator
          if (notifications.isNotEmpty()) {
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(ChromeOSBlue),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = notifications.size.toString(),
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    ShelfPosition.LEFT, ShelfPosition.RIGHT -> {
      // Vertical Shelf
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .width(52.dp)
          .background(shelfBg)
          .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        ChromeOSLauncherButton(
          isOpen = isLauncherOpen,
          size = 38.dp,
          modifier = Modifier.clickable { viewModel.toggleLauncher() }
        )

        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          viewModel.allApps.filter { it.isPinned }.forEach { app ->
            val runningWin = windows.find { it.appId == app.appId }
            val isRunning = runningWin != null
            Box(
              modifier = Modifier
                .clickable {
                  if (isRunning) {
                    if (runningWin!!.isMinimized) viewModel.bringToFront(runningWin.id)
                    else viewModel.minimizeWindow(runningWin.id)
                  } else {
                    viewModel.openApp(app.appId)
                  }
                }
            ) {
              ChromeOSAppIcon(appId = app.appId, size = 32.dp)
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(if (isQuickSettingsOpen) ChromeOSBlue.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f))
            .clickable { viewModel.toggleQuickSettings() }
            .padding(6.dp)
        ) {
          Icon(Icons.Default.Settings, contentDescription = "Quick Settings", tint = Color.White, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
