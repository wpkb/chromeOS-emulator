package com.example.chromeos.ui.quicksettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun ChromeOSQuickSettings(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val isWifi by viewModel.isWifiOn.collectAsState()
  val isBt by viewModel.isBluetoothOn.collectAsState()
  val isNightLight by viewModel.isNightLightOn.collectAsState()
  val volume by viewModel.volume.collectAsState()
  val brightness by viewModel.brightness.collectAsState()
  val hostInfo by viewModel.hostInfo.collectAsState()
  val notifications by viewModel.notifications.collectAsState()

  // Semi-transparent backdrop
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Transparent)
      .clickable { viewModel.closeQuickSettings() },
    contentAlignment = Alignment.BottomEnd
  ) {
    Card(
      modifier = Modifier
        .padding(bottom = 56.dp, end = 12.dp)
        .width(340.dp)
        .shadow(16.dp, RoundedCornerShape(16.dp))
        .clickable(enabled = false) {}, // prevent closing on internal click
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isDarkMode) Color(0xF0202124) else Color(0xF5FFFFFF)
      )
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        // Header (Profile & Action buttons)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ChromeOSBlue),
              contentAlignment = Alignment.Center
            ) {
              Text("C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Chromebook User",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color.Black
              )
              Text(
                text = "${hostInfo?.batteryPct ?: 88}% • ${if (hostInfo?.isCharging == true) "Charging" else "On Battery"}",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
              onClick = {
                viewModel.openApp(AppId.SETTINGS)
                viewModel.closeQuickSettings()
              },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }

            IconButton(
              onClick = {
                viewModel.closeQuickSettings()
              },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Toggles (Pills)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickPill(
              icon = if (isWifi) Icons.Default.Wifi else Icons.Default.WifiOff,
              label = "Wi-Fi",
              sublabel = if (isWifi) "Host WiFi" else "Off",
              isActive = isWifi,
              onClick = { viewModel.toggleWifi() },
              modifier = Modifier.weight(1f)
            )
            QuickPill(
              icon = Icons.Default.Bluetooth,
              label = "Bluetooth",
              sublabel = if (isBt) "On" else "Off",
              isActive = isBt,
              onClick = { viewModel.toggleBluetooth() },
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickPill(
              icon = Icons.Default.DarkMode,
              label = "Dark theme",
              sublabel = if (isDarkMode) "On" else "Off",
              isActive = isDarkMode,
              onClick = { viewModel.toggleDarkMode() },
              modifier = Modifier.weight(1f)
            )
            QuickPill(
              icon = Icons.Default.NightsStay,
              label = "Night Light",
              sublabel = if (isNightLight) "Warm" else "Off",
              isActive = isNightLight,
              onClick = { viewModel.toggleNightLight() },
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickPill(
              icon = Icons.Default.Terminal,
              label = "Crostini",
              sublabel = "Terminal",
              isActive = true,
              onClick = {
                viewModel.openApp(AppId.TERMINAL)
                viewModel.closeQuickSettings()
              },
              modifier = Modifier.weight(1f)
            )
            QuickPill(
              icon = Icons.Default.Screenshot,
              label = "Capture",
              sublabel = "Screen",
              isActive = false,
              onClick = {
                viewModel.openApp(AppId.CANVAS)
                viewModel.closeQuickSettings()
              },
              modifier = Modifier.weight(1f)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(10.dp))

        // Sliders (Volume & Brightness)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (volume > 0f) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
            contentDescription = null,
            tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Slider(
            value = volume,
            onValueChange = { viewModel.setVolume(it) },
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = ChromeOSBlue, activeTrackColor = ChromeOSBlue)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.BrightnessMedium,
            contentDescription = null,
            tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Slider(
            value = brightness,
            onValueChange = { viewModel.setBrightness(it) },
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = ChromeOSBlue, activeTrackColor = ChromeOSBlue)
          )
        }

        // Notification item preview
        if (notifications.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Divider(color = Color.Gray.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Notifications", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(
              "Clear all",
              fontSize = 10.sp,
              color = ChromeOSBlue,
              modifier = Modifier.clickable { viewModel.clearAllNotifications() }
            )
          }

          notifications.take(2).forEach { notif ->
            Spacer(modifier = Modifier.height(6.dp))
            Card(
              colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Color(0xFF2B2D30) else Color(0xFFF1F3F4)
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(notif.message, fontSize = 10.sp, color = Color.Gray)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun QuickPill(
  icon: ImageVector,
  label: String,
  sublabel: String,
  isActive: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg = if (isActive) ChromeOSBlue else Color.Gray.copy(alpha = 0.2f)
  val fg = if (isActive) Color.White else Color.Gray

  Row(
    modifier = modifier
      .height(44.dp)
      .clip(RoundedCornerShape(22.dp))
      .background(bg)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = fg,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg, maxLines = 1)
      Text(sublabel, fontSize = 9.sp, color = fg.copy(alpha = 0.8f), maxLines = 1)
    }
  }
}
