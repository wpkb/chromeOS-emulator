package com.example.chromeos.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSSurfaceDark

@Composable
fun CalculatorApp(isDarkMode: Boolean = true) {
  var display by remember { mutableStateOf("0") }
  var prevNumber by remember { mutableStateOf<Double?>(null) }
  var pendingOp by remember { mutableStateOf<String?>(null) }
  var resetOnNextDigit by remember { mutableStateOf(false) }

  fun onDigit(d: String) {
    if (display == "0" || resetOnNextDigit) {
      display = d
      resetOnNextDigit = false
    } else {
      display += d
    }
  }

  fun onOperator(op: String) {
    val current = display.toDoubleOrNull() ?: 0.0
    if (prevNumber != null && pendingOp != null && !resetOnNextDigit) {
      val res = when (pendingOp) {
        "+" -> prevNumber!! + current
        "-" -> prevNumber!! - current
        "×" -> prevNumber!! * current
        "÷" -> if (current != 0.0) prevNumber!! / current else 0.0
        else -> current
      }
      display = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
      prevNumber = res
    } else {
      prevNumber = current
    }
    pendingOp = op
    resetOnNextDigit = true
  }

  fun onEqual() {
    val current = display.toDoubleOrNull() ?: 0.0
    if (prevNumber != null && pendingOp != null) {
      val res = when (pendingOp) {
        "+" -> prevNumber!! + current
        "-" -> prevNumber!! - current
        "×" -> prevNumber!! * current
        "÷" -> if (current != 0.0) prevNumber!! / current else 0.0
        else -> current
      }
      display = if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
      prevNumber = null
      pendingOp = null
      resetOnNextDigit = true
    }
  }

  fun onClear() {
    display = "0"
    prevNumber = null
    pendingOp = null
    resetOnNextDigit = false
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF1F3F4))
      .padding(12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Calculator Display
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(0.8f)
        .padding(bottom = 8.dp),
      contentAlignment = Alignment.BottomEnd
    ) {
      Column(horizontalAlignment = Alignment.End) {
        if (pendingOp != null && prevNumber != null) {
          Text(
            text = "$prevNumber $pendingOp",
            color = Color.Gray,
            fontSize = 13.sp
          )
        }
        Text(
          text = display,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDarkMode) Color.White else Color.Black,
          textAlign = TextAlign.End,
          maxLines = 1
        )
      }
    }

    // Keypad Grid
    val buttons = listOf(
      listOf("C", "+/-", "%", "÷"),
      listOf("7", "8", "9", "×"),
      listOf("4", "5", "6", "-"),
      listOf("1", "2", "3", "+"),
      listOf("0", ".", "=")
    )

    Column(
      modifier = Modifier.weight(2.2f),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      buttons.forEach { row ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          row.forEach { btn ->
            val isOp = btn in listOf("÷", "×", "-", "+", "=")
            val isAction = btn in listOf("C", "+/-", "%")
            val weight = if (btn == "0") 2f else 1f

            val bg = when {
              btn == "=" -> ChromeOSBlue
              isOp -> if (isDarkMode) Color(0xFF3C4043) else Color(0xFFDFE1E5)
              isAction -> if (isDarkMode) Color(0xFF2F3133) else Color(0xFFE8EAED)
              else -> if (isDarkMode) Color(0xFF2D2F31) else Color.White
            }

            val fg = when {
              btn == "=" -> Color.White
              isOp -> ChromeOSBlue
              else -> if (isDarkMode) Color.White else Color.Black
            }

            Box(
              modifier = Modifier
                .weight(weight)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(bg)
                .clickable {
                  when (btn) {
                    "C" -> onClear()
                    "=" -> onEqual()
                    "+/-" -> {
                      val v = display.toDoubleOrNull() ?: 0.0
                      display = (-v).toString()
                    }
                    "%" -> {
                      val v = display.toDoubleOrNull() ?: 0.0
                      display = (v / 100).toString()
                    }
                    in listOf("÷", "×", "-", "+") -> onOperator(btn)
                    else -> onDigit(btn)
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = btn,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = fg
              )
            }
          }
        }
      }
    }
  }
}
