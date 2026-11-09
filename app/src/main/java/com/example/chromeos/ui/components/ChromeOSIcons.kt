package com.example.chromeos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chromeos.model.AppId
import com.example.ui.theme.*

@Composable
fun ChromeOSAppIcon(
  appId: AppId,
  size: Dp = 40.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape),
    contentAlignment = Alignment.Center
  ) {
    when (appId) {
      AppId.CHROME -> ChromeBrowserIcon(size = size)
      AppId.TERMINAL -> CrostiniTerminalIcon(size = size)
      AppId.FILES -> FilesAppIcon(size = size)
      AppId.SETTINGS -> SettingsAppIcon(size = size)
      AppId.WEB_STORE -> WebStoreIcon(size = size)
      AppId.CALCULATOR -> CalculatorAppIcon(size = size)
      AppId.TEXT_EDITOR -> TextEditorIcon(size = size)
      AppId.CANVAS -> CanvasAppIcon(size = size)
      AppId.TASK_MANAGER -> TaskManagerIcon(size = size)
      AppId.DIAGNOSTICS -> DiagnosticsIcon(size = size)
      AppId.CAMERA -> CameraAppIcon(size = size)
    }
  }
}

@Composable
fun ChromeOSLauncherButton(
  isOpen: Boolean,
  size: Dp = 44.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(if (isOpen) ChromeOSBlue.copy(alpha = 0.35f) else Color.Transparent),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * 0.55f)) {
      val center = Offset(size.toPx() * 0.275f, size.toPx() * 0.275f)
      val radius = size.toPx() * 0.24f

      // Outer ring
      drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.5.dp.toPx())
      )

      // Inner filled circle
      drawCircle(
        color = Color.White,
        radius = radius * 0.42f,
        center = center
      )
    }
  }
}

@Composable
fun ChromeBrowserIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color.White),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * 0.88f)) {
      val center = Offset(this.size.width / 2, this.size.height / 2)
      val radius = this.size.width / 2

      // Red segment (top)
      drawArc(
        color = ChromeOSRed,
        startAngle = -150f,
        sweepAngle = 120f,
        useCenter = true
      )
      // Green segment (bottom-right)
      drawArc(
        color = ChromeOSGreen,
        startAngle = -30f,
        sweepAngle = 120f,
        useCenter = true
      )
      // Yellow segment (bottom-left)
      drawArc(
        color = ChromeOSYellow,
        startAngle = 90f,
        sweepAngle = 120f,
        useCenter = true
      )
      // Inner white boundary
      drawCircle(
        color = Color.White,
        radius = radius * 0.48f,
        center = center
      )
      // Inner blue core
      drawCircle(
        color = ChromeOSBlue,
        radius = radius * 0.40f,
        center = center
      )
    }
  }
}

@Composable
fun CrostiniTerminalIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(CrostiniBg),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Terminal,
      contentDescription = "Crostini Terminal",
      tint = CrostiniGreen,
      modifier = Modifier.size(size * 0.62f)
    )
  }
}

@Composable
fun FilesAppIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF1A73E8)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Folder,
      contentDescription = "Files",
      tint = Color.White,
      modifier = Modifier.size(size * 0.62f)
    )
  }
}

@Composable
fun SettingsAppIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF5F6368)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Settings,
      contentDescription = "Settings",
      tint = Color.White,
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun WebStoreIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF202124)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Storefront,
      contentDescription = "Web Store",
      tint = Color(0xFF8AB4F8),
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun CalculatorAppIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF137333)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Calculate,
      contentDescription = "Calculator",
      tint = Color.White,
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun TextEditorIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF185ABC)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.EditNote,
      contentDescription = "Text Editor",
      tint = Color.White,
      modifier = Modifier.size(size * 0.62f)
    )
  }
}

@Composable
fun CanvasAppIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFFE37400)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Brush,
      contentDescription = "Canvas",
      tint = Color.White,
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun TaskManagerIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF3C4043)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Memory,
      contentDescription = "Task Manager",
      tint = Color(0xFF81C995),
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun DiagnosticsIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF1A73E8)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Speed,
      contentDescription = "Diagnostics",
      tint = Color.White,
      modifier = Modifier.size(size * 0.60f)
    )
  }
}

@Composable
fun CameraAppIcon(size: Dp = 40.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(0xFF202124)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.PhotoCamera,
      contentDescription = "Camera",
      tint = Color.White,
      modifier = Modifier.size(size * 0.60f)
    )
  }
}
