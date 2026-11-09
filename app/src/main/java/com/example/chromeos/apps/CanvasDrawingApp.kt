package com.example.chromeos.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class DrawPath(
  val points: List<Offset>,
  val color: Color,
  val strokeWidth: Float
)

@Composable
fun CanvasDrawingApp(isDarkMode: Boolean = true) {
  var paths by remember { mutableStateOf(listOf<DrawPath>()) }
  var currentPoints by remember { mutableStateOf(listOf<Offset>()) }
  var currentColor by remember { mutableStateOf(ChromeOSBlue) }
  var strokeWidth by remember { mutableStateOf(6f) }
  var isEraser by remember { mutableStateOf(false) }

  val palette = listOf(
    ChromeOSBlue,
    ChromeOSRed,
    ChromeOSYellow,
    ChromeOSGreen,
    Color.White,
    Color.Black
  )

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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        palette.forEach { c ->
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(c)
              .border(
                width = if (currentColor == c && !isEraser) 2.5.dp else 1.dp,
                color = if (currentColor == c && !isEraser) ChromeOSBlue else Color.Gray,
                shape = CircleShape
              )
              .clickable {
                currentColor = c
                isEraser = false
              }
          )
        }

        FilterChip(
          selected = isEraser,
          onClick = { isEraser = !isEraser },
          label = { Text("Eraser", fontSize = 10.sp) }
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { paths = emptyList() },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
      }
    }

    // Drawing Canvas
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .pointerInput(isEraser, currentColor, strokeWidth) {
          detectDragGestures(
            onDragStart = { offset ->
              currentPoints = listOf(offset)
            },
            onDrag = { change, _ ->
              currentPoints = currentPoints + change.position
            },
            onDragEnd = {
              if (currentPoints.isNotEmpty()) {
                val drawColor = if (isEraser) Color.White else currentColor
                val drawWidth = if (isEraser) strokeWidth * 3f else strokeWidth
                paths = paths + DrawPath(currentPoints, drawColor, drawWidth)
                currentPoints = emptyList()
              }
            }
          )
        }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw existing paths
        paths.forEach { path ->
          for (i in 0 until path.points.size - 1) {
            drawLine(
              color = path.color,
              start = path.points[i],
              end = path.points[i + 1],
              strokeWidth = path.strokeWidth,
              cap = StrokeCap.Round
            )
          }
        }

        // Draw active stroke
        val activeColor = if (isEraser) Color.White else currentColor
        val activeWidth = if (isEraser) strokeWidth * 3f else strokeWidth
        for (i in 0 until currentPoints.size - 1) {
          drawLine(
            color = activeColor,
            start = currentPoints[i],
            end = currentPoints[i + 1],
            strokeWidth = activeWidth,
            cap = StrokeCap.Round
          )
        }
      }
    }
  }
}
