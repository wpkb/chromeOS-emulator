package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.VFile
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun TextEditorApp(
  initialText: String? = null,
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  var content by remember {
    mutableStateOf(
      initialText ?: "# Untitled Document\n\nWelcome to ChromeOS Text Editor.\nYou can write notes, markdown, and code here.\n"
    )
  }
  var fileName by remember { mutableStateOf("notes_${System.currentTimeMillis() % 1000}.txt") }
  var saveMessage by remember { mutableStateOf<String?>(null) }

  val wordCount = remember(content) {
    if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size
  }
  val charCount = remember(content) { content.length }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF1E1E1E) else Color.White)
  ) {
    // Toolbar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) ChromeOSSurfaceDark else Color(0xFFF1F3F4))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = fileName,
          fontWeight = FontWeight.Medium,
          fontSize = 12.sp,
          color = if (isDarkMode) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "$wordCount words • $charCount chars",
          fontSize = 10.sp,
          color = Color.Gray
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
          onClick = {
            viewModel.addFile(
              VFile(
                id = "doc_${System.currentTimeMillis()}",
                name = fileName,
                path = "/My files/Downloads/$fileName",
                isFolder = false,
                content = content,
                sizeBytes = content.toByteArray().size.toLong(),
                category = "Downloads"
              )
            )
            saveMessage = "Saved to Downloads!"
          },
          colors = ButtonDefaults.buttonColors(containerColor = ChromeOSBlue),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(28.dp)
        ) {
          Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Save", fontSize = 10.sp)
        }
      }
    }

    if (saveMessage != null) {
      Text(
        text = saveMessage!!,
        fontSize = 10.sp,
        color = Color(0xFF81C995),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
      )
    }

    // Editor Area
    BasicTextField(
      value = content,
      onValueChange = {
        content = it
        saveMessage = null
      },
      textStyle = TextStyle(
        color = if (isDarkMode) Color(0xFFD4D4D4) else Color(0xFF202124),
        fontSize = 13.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 18.sp
      ),
      cursorBrush = SolidColor(ChromeOSBlue),
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
    )
  }
}
