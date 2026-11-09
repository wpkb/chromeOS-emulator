package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

data class ProcessItem(
  val name: String,
  val appId: AppId?,
  val cpu: String,
  val memory: String,
  val pid: Int
)

@Composable
fun TaskManagerApp(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val windows by viewModel.windows.collectAsState()
  var selectedPid by remember { mutableStateOf<Int?>(null) }

  val processes = remember(windows) {
    val list = mutableListOf(
      ProcessItem("Browser (ChromeOS Shell & Aura)", null, "1.2%", "142 MB", 101),
      ProcessItem("GPU Process (Vulkan/GLES)", null, "0.8%", "88 MB", 104),
      ProcessItem("Crostini Linux Container (penguin)", AppId.TERMINAL, "0.4%", "120 MB", 215),
      ProcessItem("Files Daemon", AppId.FILES, "0.1%", "45 MB", 320)
    )
    windows.forEachIndexed { idx, win ->
      if (list.none { it.appId == win.appId }) {
        list.add(
          ProcessItem(
            name = win.title,
            appId = win.appId,
            cpu = "${(0.3 + (idx * 0.4)).toString().take(3)}%",
            memory = "${65 + (idx * 25)} MB",
            pid = 400 + idx
          )
        )
      }
    }
    list
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF8F9FA))
  ) {
    // Header Stats
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) ChromeOSSurfaceDark else Color.White)
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Task Manager", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("Active processes: ${processes.size}", fontSize = 10.sp, color = Color.Gray)
      }

      val selected = processes.find { it.pid == selectedPid }
      Button(
        onClick = {
          selected?.appId?.let { viewModel.killProcessByAppId(it) }
          selectedPid = null
        },
        enabled = selected != null && selected.appId != null,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.height(30.dp)
      ) {
        Text("End Process", fontSize = 11.sp)
      }
    }

    Divider(color = Color.Gray.copy(alpha = 0.2f))

    // Table Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) Color(0xFF1B1C1E) else Color(0xFFEDEFEF))
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Text("Task / Process", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1.8f))
      Text("PID", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.6f))
      Text("CPU", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.7f))
      Text("Memory", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(0.9f))
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      items(processes) { proc ->
        val isSelected = selectedPid == proc.pid
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) ChromeOSBlue.copy(alpha = 0.25f) else Color.Transparent)
            .clickable { selectedPid = proc.pid }
            .padding(horizontal = 6.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(proc.name, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
          Text(proc.pid.toString(), fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.6f))
          Text(proc.cpu, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.7f))
          Text(proc.memory, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(0.9f))
        }
      }
    }
  }
}
