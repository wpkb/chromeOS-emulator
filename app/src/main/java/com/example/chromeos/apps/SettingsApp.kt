package com.example.chromeos.apps

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.ChromeOSWallpaper
import com.example.chromeos.model.ShelfPosition
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSGreen
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun SettingsApp(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  var selectedTab by remember { mutableStateOf("about") }
  val hostInfo by viewModel.hostInfo.collectAsState()
  val currentWallpaper by viewModel.currentWallpaper.collectAsState()
  val shelfPos by viewModel.shelfPosition.collectAsState()
  val isAutoHide by viewModel.isShelfAutoHide.collectAsState()
  val isNightLight by viewModel.isNightLightOn.collectAsState()
  val isWifi by viewModel.isWifiOn.collectAsState()
  val isBt by viewModel.isBluetoothOn.collectAsState()
  val volume by viewModel.volume.collectAsState()
  val displayScale by viewModel.displayScaling.collectAsState()

  var isCheckingUpdate by remember { mutableStateOf(false) }
  var updateStatusText by remember { mutableStateOf("Your Chromebook is up to date (Version 130.0.6723.102)") }

  Row(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF8F9FA))
  ) {
    // Left Navigation Sidebar
    Column(
      modifier = Modifier
        .width(180.dp)
        .fillMaxHeight()
        .background(if (isDarkMode) Color(0xFF1B1C1E) else Color(0xFFEDEFEF))
        .padding(8.dp)
    ) {
      Text(
        text = "Settings",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
      )

      val tabs = listOf(
        Pair("about", "About ChromeOS" to Icons.Default.Info),
        Pair("personalization", "Personalization" to Icons.Default.Palette),
        Pair("network", "Network" to Icons.Default.Wifi),
        Pair("bluetooth", "Bluetooth" to Icons.Default.Bluetooth),
        Pair("device", "Device & Display" to Icons.Default.Laptop),
        Pair("crostini", "Linux (Crostini)" to Icons.Default.Terminal)
      )

      tabs.forEach { (key, pair) ->
        val (label, icon) = pair
        val isSelected = selectedTab == key
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ChromeOSBlue.copy(alpha = 0.25f) else Color.Transparent)
            .clickable { selectedTab = key }
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) ChromeOSBlue else (if (isDarkMode) Color.LightGray else Color.DarkGray),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) ChromeOSBlue else (if (isDarkMode) Color.White else Color.Black)
          )
        }
      }
    }

    // Right Content Pane
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      when (selectedTab) {
        "about" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(40.dp)
                      .clip(CircleShape)
                      .background(ChromeOSBlue),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.LaptopChromebook, contentDescription = null, tint = Color.White)
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "Google ChromeOS",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isDarkMode) Color.White else Color.Black
                    )
                    Text(
                      text = "Version 130.0.6723.102 (Official Build) 64-bit",
                      fontSize = 11.sp,
                      color = Color.Gray
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // Android 7.0+ Compatibility Badge
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChromeOSGreen.copy(alpha = 0.15f))
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ChromeOSGreen,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Android 7.0+ Minimum Requirement Certified",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = ChromeOSGreen
                    )
                    Text(
                      text = "Min SDK: 24 (Android 7.0 Nougat) | Running on Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                      fontSize = 10.sp,
                      color = if (isDarkMode) Color.LightGray else Color.DarkGray
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Host specs
                hostInfo?.let { h ->
                  Text("Host System Specifications", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text("• Device: ${h.manufacturer} ${h.deviceModel}", fontSize = 11.sp, color = Color.Gray)
                  Text("• Architecture: ${h.cpuAbi}", fontSize = 11.sp, color = Color.Gray)
                  Text("• Total RAM: ${h.totalRamMb} MB (${h.availableRamMb} MB free)", fontSize = 11.sp, color = Color.Gray)
                  Text("• Battery: ${h.batteryPct}% ${if (h.isCharging) "(Charging)" else ""}", fontSize = 11.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = {
                    isCheckingUpdate = true
                    updateStatusText = "Checking for updates..."
                    // simulate quick check
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ChromeOSBlue)
                ) {
                  Text("Check for updates", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(updateStatusText, fontSize = 10.sp, color = Color.Gray)
              }
            }
          }
        }

        "personalization" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Wallpaper", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  ChromeOSWallpaper.values().forEach { wp ->
                    val isCurrent = wp == currentWallpaper
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrent) ChromeOSBlue.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.setWallpaper(wp) }
                        .padding(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Box(
                        modifier = Modifier
                          .size(32.dp, 20.dp)
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(wp.accentColor))
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(wp.title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(wp.subtitle, fontSize = 9.sp, color = Color.Gray)
                      }
                      Spacer(modifier = Modifier.weight(1f))
                      if (isCurrent) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = ChromeOSBlue, modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Dark Mode Theme", fontSize = 12.sp)
                  Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() })
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Night Light (Warm Screen)", fontSize = 12.sp)
                  Switch(checked = isNightLight, onCheckedChange = { viewModel.toggleNightLight() })
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Shelf Position", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf(ShelfPosition.BOTTOM, ShelfPosition.LEFT, ShelfPosition.RIGHT).forEach { pos ->
                    FilterChip(
                      selected = shelfPos == pos,
                      onClick = { viewModel.setShelfPosition(pos) },
                      label = { Text(pos.name.lowercase().capitalize(), fontSize = 10.sp) }
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Auto-hide shelf", fontSize = 12.sp)
                  Switch(checked = isAutoHide, onCheckedChange = { viewModel.toggleShelfAutoHide() })
                }
              }
            }
          }
        }

        "network" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Wi-Fi", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(if (isWifi) "Connected to Virtual_Host_WiFi" else "Disconnected", fontSize = 10.sp, color = Color.Gray)
                  }
                  Switch(checked = isWifi, onCheckedChange = { viewModel.toggleWifi() })
                }
                if (isWifi) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text("• IP Address: 192.168.1.105", fontSize = 10.sp, color = Color.Gray)
                  Text("• Gateway: 192.168.1.1 (NAT Bridge)", fontSize = 10.sp, color = Color.Gray)
                  Text("• Security: WPA3-Personal", fontSize = 10.sp, color = Color.Gray)
                }
              }
            }
          }
        }

        "bluetooth" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Bluetooth", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  Switch(checked = isBt, onCheckedChange = { viewModel.toggleBluetooth() })
                }
                Text("Pair wireless keyboards, mouse, and headphones", fontSize = 10.sp, color = Color.Gray)
              }
            }
          }
        }

        "device" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Audio & Volume", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Slider(value = volume, onValueChange = { viewModel.setVolume(it) })
                Text("${(volume * 100).toInt()}%", fontSize = 10.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(14.dp))
                Text("Display Resolution Scaling", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf(0.85f to "85%", 1.0f to "100%", 1.15f to "115%").forEach { (scale, label) ->
                    FilterChip(
                      selected = displayScale == scale,
                      onClick = { viewModel.setDisplayScaling(scale) },
                      label = { Text(label, fontSize = 10.sp) }
                    )
                  }
                }
              }
            }
          }
        }

        "crostini" -> {
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Linux Development Environment (Crostini)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Run Linux tools, editors, and IDEs on your Chromebook", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Text("• Container: penguin (Debian 12 Bookworm)", fontSize = 11.sp, color = Color.Gray)
                Text("• Virtual Disk: 10.0 GB allocated (3.2 GB in use)", fontSize = 11.sp, color = Color.Gray)
                Text("• Port forwarding: Enabled for localhost:3000, 8080", fontSize = 11.sp, color = Color.Gray)
              }
            }
          }
        }
      }
    }
  }
}
