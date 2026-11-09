package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

data class WebStoreItem(
  val name: String,
  val category: String,
  val url: String,
  val rating: String,
  val description: String,
  val icon: ImageVector,
  val iconBg: Color
)

@Composable
fun WebStoreApp(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val storeApps = listOf(
    WebStoreItem("Google Docs", "Productivity", "https://docs.google.com", "4.8 ★", "Online document editor with real-time collaboration", Icons.Default.Description, Color(0xFF4285F4)),
    WebStoreItem("Google Sheets", "Productivity", "https://sheets.google.com", "4.7 ★", "Powerful online spreadsheets and charts", Icons.Default.TableChart, Color(0xFF0F9D58)),
    WebStoreItem("YouTube", "Entertainment", "https://www.youtube.com", "4.9 ★", "Watch videos, music, and streams in high definition", Icons.Default.PlayCircle, Color(0xFFEA4335)),
    WebStoreItem("GitHub", "Development", "https://github.com", "4.9 ★", "Software development and version control platform", Icons.Default.Code, Color(0xFF24292E)),
    WebStoreItem("VS Code for Web", "Development", "https://vscode.dev", "4.9 ★", "Visual Studio Code editor right in your browser", Icons.Default.Terminal, Color(0xFF007ACC)),
    WebStoreItem("Wikipedia", "Reference", "https://en.wikipedia.org", "4.8 ★", "The free knowledge encyclopedia", Icons.Default.Book, Color(0xFF757575)),
    WebStoreItem("Reddit", "Social", "https://www.reddit.com", "4.5 ★", "Dive into anything with communities on every topic", Icons.Default.Forum, Color(0xFFFF4500)),
    WebStoreItem("Spotify Web", "Music", "https://open.spotify.com", "4.8 ★", "Music streaming for every mood and moment", Icons.Default.MusicNote, Color(0xFF1DB954))
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF8F9FA))
  ) {
    // Store Banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(ChromeOSBlue)
        .padding(16.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text("Chrome Web Store & PWA Hub", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
          Text("Optimized desktop web applications for ChromeOS", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
        }
      }
    }

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 200.dp),
      contentPadding = PaddingValues(12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(storeApps) { app ->
        Card(
          colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ChromeOSSurfaceDark else Color.White),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                .background(app.iconBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(app.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(app.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${app.category} • ${app.rating}", fontSize = 10.sp, color = Color.Gray)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(app.description, fontSize = 10.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = {
                viewModel.openApp(AppId.CHROME, payload = app.url)
              },
              colors = ButtonDefaults.buttonColors(containerColor = ChromeOSBlue),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
            ) {
              Text("Launch in Chrome", fontSize = 10.sp)
            }
          }
        }
      }
    }
  }
}
