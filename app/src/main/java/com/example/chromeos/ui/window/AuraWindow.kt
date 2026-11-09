package com.example.chromeos.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.chromeos.apps.*
import com.example.chromeos.model.AppId
import com.example.chromeos.model.ChromeOSWindow
import com.example.chromeos.model.WindowSnapState
import com.example.chromeos.ui.components.ChromeOSAppIcon
import com.example.chromeos.viewmodel.ChromeOSViewModel
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSTitlebarDark
import com.example.ui.theme.ChromeOSTitlebarLight
import kotlin.math.roundToInt

@Composable
fun AuraWindow(
  window: ChromeOSWindow,
  viewModel: ChromeOSViewModel,
  containerWidthDp: Float,
  containerHeightDp: Float,
  isDarkMode: Boolean = true
) {
  if (window.isMinimized) return

  // Calculate effective position and size based on snapState
  val isMaximized = window.snapState == WindowSnapState.MAXIMIZED
  val isSnapLeft = window.snapState == WindowSnapState.LEFT_HALF
  val isSnapRight = window.snapState == WindowSnapState.RIGHT_HALF

  val effectiveOffset: Offset
  val effectiveWidthDp: Float
  val effectiveHeightDp: Float

  when {
    isMaximized -> {
      effectiveOffset = Offset(0f, 0f)
      effectiveWidthDp = containerWidthDp
      effectiveHeightDp = containerHeightDp
    }
    isSnapLeft -> {
      effectiveOffset = Offset(0f, 0f)
      effectiveWidthDp = containerWidthDp / 2f
      effectiveHeightDp = containerHeightDp
    }
    isSnapRight -> {
      effectiveOffset = Offset(containerWidthDp / 2f, 0f)
      effectiveWidthDp = containerWidthDp / 2f
      effectiveHeightDp = containerHeightDp
    }
    else -> {
      effectiveOffset = window.position
      effectiveWidthDp = window.size.first.value.coerceAtMost(containerWidthDp)
      effectiveHeightDp = window.size.second.value.coerceAtMost(containerHeightDp)
    }
  }

  val windowShape = if (isMaximized || isSnapLeft || isSnapRight) {
    RoundedCornerShape(0.dp)
  } else {
    RoundedCornerShape(12.dp)
  }

  Box(
    modifier = Modifier
      .offset { IntOffset(effectiveOffset.x.roundToInt(), effectiveOffset.y.roundToInt()) }
      .size(effectiveWidthDp.dp, effectiveHeightDp.dp)
      .zIndex(window.zIndex)
      .shadow(elevation = if (isMaximized) 0.dp else 12.dp, shape = windowShape)
      .clip(windowShape)
      .background(if (isDarkMode) Color(0xFF202124) else Color.White)
      .border(
        width = 1.dp,
        color = if (isDarkMode) Color(0xFF3C4043) else Color(0xFFDFE1E5),
        shape = windowShape
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        viewModel.bringToFront(window.id)
      }
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // ChromeOS Window Titlebar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .background(if (isDarkMode) ChromeOSTitlebarDark else ChromeOSTitlebarLight)
          .pointerInput(window.id, window.snapState) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              viewModel.updateWindowPosition(window.id, dragAmount)
            }
          }
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // App icon & title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          ChromeOSAppIcon(appId = window.appId, size = 18.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = window.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkMode) Color.White else Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Window Controls (Snap, Minimize, Maximize, Close)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          // Snap Left
          IconButton(
            onClick = {
              val next = if (window.snapState == WindowSnapState.LEFT_HALF) WindowSnapState.NONE else WindowSnapState.LEFT_HALF
              viewModel.snapWindow(window.id, next)
            },
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VerticalSplit,
              contentDescription = "Snap Left",
              tint = if (isSnapLeft) ChromeOSBlue else (if (isDarkMode) Color.LightGray else Color.DarkGray),
              modifier = Modifier.size(14.dp)
            )
          }

          // Minimize
          IconButton(
            onClick = { viewModel.minimizeWindow(window.id) },
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Minimize",
              tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
              modifier = Modifier.size(14.dp)
            )
          }

          // Maximize / Restore
          IconButton(
            onClick = { viewModel.toggleMaximizeWindow(window.id) },
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = if (isMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
              contentDescription = "Maximize",
              tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
              modifier = Modifier.size(13.dp)
            )
          }

          // Close
          IconButton(
            onClick = { viewModel.closeWindow(window.id) },
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Window Content (Child App)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        when (window.appId) {
          AppId.CHROME -> ChromeBrowserApp(
            initialUrl = window.extraPayload,
            isDarkMode = isDarkMode
          )
          AppId.TERMINAL -> CrostiniTerminalApp(viewModel = viewModel)
          AppId.FILES -> FilesManagerApp(viewModel = viewModel, isDarkMode = isDarkMode)
          AppId.SETTINGS -> SettingsApp(viewModel = viewModel, isDarkMode = isDarkMode)
          AppId.TASK_MANAGER -> TaskManagerApp(viewModel = viewModel, isDarkMode = isDarkMode)
          AppId.CALCULATOR -> CalculatorApp(isDarkMode = isDarkMode)
          AppId.TEXT_EDITOR -> TextEditorApp(
            initialText = window.extraPayload,
            viewModel = viewModel,
            isDarkMode = isDarkMode
          )
          AppId.CANVAS -> CanvasDrawingApp(isDarkMode = isDarkMode)
          AppId.WEB_STORE -> WebStoreApp(viewModel = viewModel, isDarkMode = isDarkMode)
          AppId.DIAGNOSTICS -> SettingsApp(viewModel = viewModel, isDarkMode = isDarkMode)
          AppId.CAMERA -> ChromeBrowserApp(initialUrl = "https://webcamtests.com", isDarkMode = isDarkMode)
        }
      }
    }

    // Resize Corner Handle (only shown when not maximized or snapped)
    if (!isMaximized && !isSnapLeft && !isSnapRight) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(18.dp)
          .pointerInput(window.id) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              viewModel.updateWindowSize(window.id, dragAmount.x.dp, dragAmount.y.dp)
            }
          },
        contentAlignment = Alignment.BottomEnd
      ) {
        Icon(
          imageVector = Icons.Default.SouthEast,
          contentDescription = "Resize",
          tint = Color.Gray.copy(alpha = 0.6f),
          modifier = Modifier.size(12.dp)
        )
      }
    }
  }
}
