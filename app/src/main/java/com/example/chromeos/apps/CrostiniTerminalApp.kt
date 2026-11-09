package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.LineType
import com.example.chromeos.model.TerminalLine
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.*

@Composable
fun CrostiniTerminalApp(
  viewModel: ChromeOSViewModel
) {
  val lines by viewModel.terminalLines.collectAsState()
  val currentDir by viewModel.terminalCurrentDir.collectAsState()
  var inputCommand by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Scroll to bottom on new line
  LaunchedEffect(lines.size) {
    if (lines.isNotEmpty()) {
      listState.animateScrollToItem(lines.size - 1)
    }
  }

  fun sendCommand(cmd: String) {
    val toRun = cmd.trim()
    if (toRun.isNotEmpty()) {
      viewModel.executeTerminalCommand(toRun)
      inputCommand = ""
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CrostiniBg)
  ) {
    // Top terminal status pill
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF2B2B2B))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CrostiniGreen)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "penguin (Debian Bookworm / Linux 5.15)",
          color = Color.LightGray,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }
      Text(
        text = "Crostini Active",
        color = CrostiniCyan,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }

    // Scrollable output area
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      items(lines) { line ->
        TerminalLineView(line)
      }
    }

    // Quick Command Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF262626))
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      val quickCommands = listOf("help", "neofetch", "uname -a", "ls -la", "top", "free -m", "date", "python3 2**16", "clear")
      quickCommands.forEach { cmd ->
        AssistChip(
          onClick = { sendCommand(cmd) },
          label = {
            Text(
              cmd,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = CrostiniCyan
            )
          },
          modifier = Modifier.height(26.dp)
        )
      }
    }

    // Terminal Input Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF141414))
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "penguin@crostini:$currentDir$ ",
        color = CrostiniGreen,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )

      BasicTextField(
        value = inputCommand,
        onValueChange = { inputCommand = it },
        singleLine = true,
        textStyle = TextStyle(
          color = Color.White,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        ),
        cursorBrush = SolidColor(CrostiniGreen),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = { sendCommand(inputCommand) }),
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 4.dp)
      )

      IconButton(
        onClick = { sendCommand(inputCommand) },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Run",
          tint = CrostiniGreen,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun TerminalLineView(line: TerminalLine) {
  val color = when (line.type) {
    LineType.COMMAND -> CrostiniGreen
    LineType.OUTPUT -> Color(0xFFE0E0E0)
    LineType.ERROR -> ChromeOSRed
    LineType.SUCCESS -> Color(0xFF81C995)
    LineType.SYSTEM -> CrostiniCyan
    LineType.INFO -> CrostiniYellow
    LineType.ASCII -> CrostiniBlue
  }

  Text(
    text = line.text,
    color = color,
    fontSize = 11.sp,
    fontFamily = FontFamily.Monospace,
    fontWeight = if (line.type == LineType.COMMAND) FontWeight.Bold else FontWeight.Normal,
    lineHeight = 15.sp
  )
}
