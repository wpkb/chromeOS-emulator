package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chromeos.model.AppId
import com.example.chromeos.model.VFile
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun FilesManagerApp(
  viewModel: ChromeOSViewModel,
  isDarkMode: Boolean = true
) {
  val allFiles by viewModel.files.collectAsState()
  var selectedCategory by remember { mutableStateOf("Downloads") }
  var selectedFile by remember { mutableStateOf<VFile?>(null) }
  var showNewFileDialog by remember { mutableStateOf(false) }
  var newFileName by remember { mutableStateOf("") }

  val filteredFiles = remember(allFiles, selectedCategory) {
    allFiles.filter { it.category == selectedCategory }
  }

  Row(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF8F9FA))
  ) {
    // Left Sidebar (Navigation Tree)
    Column(
      modifier = Modifier
        .width(160.dp)
        .fillMaxHeight()
        .background(if (isDarkMode) Color(0xFF1B1C1E) else Color(0xFFEDEFEF))
        .padding(8.dp)
    ) {
      Text(
        text = "Files",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isDarkMode) Color.White else Color.Black,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
      )

      val categories = listOf(
        Triple("Downloads", Icons.Default.Download, "Downloads"),
        Triple("Google Drive", Icons.Default.CloudQueue, "Google Drive"),
        Triple("Linux files", Icons.Default.Terminal, "Linux files")
      )

      categories.forEach { (catName, icon, label) ->
        val isSelected = selectedCategory == catName
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ChromeOSBlue.copy(alpha = 0.25f) else Color.Transparent)
            .clickable {
              selectedCategory = catName
              selectedFile = null
            }
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

      Spacer(modifier = Modifier.weight(1f))

      // Storage indicator
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(if (isDarkMode) Color(0xFF26282B) else Color.White)
          .padding(8.dp)
      ) {
        Text("Internal Storage", fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { 0.35f },
          modifier = Modifier.fillMaxWidth().height(4.dp),
          color = ChromeOSBlue,
          trackColor = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("42.8 GB available", fontSize = 9.sp, color = Color.Gray)
      }
    }

    // Right Content Area
    Column(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    ) {
      // Top Action Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(if (isDarkMode) ChromeOSSurfaceDark else Color.White)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "My files > $selectedCategory",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = if (isDarkMode) Color.White else Color.Black
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Button(
            onClick = {
              newFileName = "Notes_${System.currentTimeMillis() % 1000}.txt"
              showNewFileDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = ChromeOSBlue),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("New file", fontSize = 11.sp)
          }

          if (selectedFile != null) {
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
              onClick = {
                viewModel.deleteFile(selectedFile!!.id)
                selectedFile = null
              },
              modifier = Modifier.size(30.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      Divider(color = if (isDarkMode) Color(0xFF333538) else Color(0xFFE0E0E0))

      // File List
      if (filteredFiles.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            "This folder is empty",
            color = Color.Gray,
            fontSize = 12.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
        ) {
          items(filteredFiles) { file ->
            val isSelected = selectedFile?.id == file.id
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) ChromeOSBlue.copy(alpha = 0.2f) else Color.Transparent)
                .clickable { selectedFile = file }
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (file.isFolder) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (file.isFolder) ChromeOSBlue else (if (isDarkMode) Color.LightGray else Color.DarkGray),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = file.name,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isDarkMode) Color.White else Color.Black,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${file.sizeBytes} B • ${file.modified}",
                  fontSize = 10.sp,
                  color = Color.Gray
                )
              }
              if (!file.isFolder && isSelected) {
                TextButton(
                  onClick = {
                    viewModel.openApp(AppId.TEXT_EDITOR, payload = file.content)
                  },
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("Open in Text", fontSize = 10.sp, color = ChromeOSBlue)
                }
              }
            }
          }
        }
      }
    }
  }

  // Create New File Dialog
  if (showNewFileDialog) {
    AlertDialog(
      onDismissRequest = { showNewFileDialog = false },
      title = { Text("Create New Text File", fontSize = 14.sp) },
      text = {
        OutlinedTextField(
          value = newFileName,
          onValueChange = { newFileName = it },
          label = { Text("File Name") },
          singleLine = true
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            if (newFileName.isNotBlank()) {
              viewModel.addFile(
                VFile(
                  id = "file_${System.currentTimeMillis()}",
                  name = newFileName,
                  path = "/My files/$selectedCategory/$newFileName",
                  isFolder = false,
                  content = "# ${newFileName}\nCreated in ChromeOS Files\n",
                  sizeBytes = 64,
                  category = selectedCategory
                )
              )
            }
            showNewFileDialog = false
          }
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewFileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
