package com.example.chromeos.ui.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.ui.components.ChromeOSAppIcon
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun ChromeOSLauncher(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val allApps = viewModel.allApps

  val filteredApps = remember(searchQuery) {
    if (searchQuery.isBlank()) allApps
    else allApps.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.description.contains(searchQuery, ignoreCase = true) ||
      it.category.contains(searchQuery, ignoreCase = true)
    }
  }

  // Semi-transparent backdrop
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.35f))
      .clickable { viewModel.closeLauncher() },
    contentAlignment = Alignment.BottomStart
  ) {
    // Launcher Card
    Card(
      modifier = Modifier
        .padding(start = 16.dp, bottom = 60.dp, end = 16.dp)
        .widthIn(max = 540.dp)
        .heightIn(max = 520.dp)
        .shadow(16.dp, RoundedCornerShape(20.dp))
        .clickable(enabled = false) {}, // prevent click-through
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isDarkMode) Color(0xF01E2022) else Color(0xF5F6F8FC)
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Search bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (isDarkMode) Color(0xFF2E3134) else Color(0xFFE3E5E8))
            .padding(horizontal = 14.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TextField(
              value = searchQuery,
              onValueChange = { viewModel.setSearchQuery(it) },
              placeholder = {
                Text(
                  "Search your device, apps, web...",
                  fontSize = 13.sp,
                  color = Color.Gray
                )
              },
              singleLine = true,
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
              ),
              modifier = Modifier.weight(1f)
            )
            if (searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { viewModel.setSearchQuery("") },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Suggested / Recent Apps
        if (searchQuery.isBlank()) {
          Text(
            text = "Suggested",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf(AppId.CHROME, AppId.FILES, AppId.TERMINAL, AppId.SETTINGS, AppId.WEB_STORE).forEach { appId ->
              val app = allApps.find { it.appId == appId } ?: return@forEach
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { viewModel.openApp(appId) }
                  .padding(8.dp)
              ) {
                ChromeOSAppIcon(appId = appId, size = 40.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = app.name,
                  fontSize = 10.sp,
                  color = if (isDarkMode) Color.White else Color.Black,
                  maxLines = 1
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Divider(color = Color.Gray.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "All Apps",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
          )
        }

        // Grid of Apps
        LazyVerticalGrid(
          columns = GridCells.Fixed(4),
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredApps) { app ->
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.openApp(app.appId) }
                .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
              ChromeOSAppIcon(appId = app.appId, size = 44.dp)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = app.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isDarkMode) Color.White else Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }
  }
}
