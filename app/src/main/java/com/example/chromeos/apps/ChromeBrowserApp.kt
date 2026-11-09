package com.example.chromeos.apps

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.ChromeOSBlue
import com.example.ui.theme.ChromeOSTitlebarDark
import java.net.URLEncoder

data class ChromeTab(
  val id: String,
  val title: String,
  val url: String
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChromeBrowserApp(
  initialUrl: String? = null,
  isDarkMode: Boolean = true
) {
  var tabs by remember {
    mutableStateOf(
      listOf(
        ChromeTab(
          id = "tab_1",
          title = "Google",
          url = initialUrl ?: "https://www.google.com"
        )
      )
    )
  }
  var activeTabId by remember { mutableStateOf("tab_1") }
  val activeTab = tabs.find { it.id == activeTabId } ?: tabs.first()

  var urlInput by remember(activeTab.url) { mutableStateOf(activeTab.url) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var canGoBack by remember { mutableStateOf(false) }
  var canGoForward by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current

  fun navigateTo(raw: String) {
    var target = raw.trim()
    if (target.isEmpty()) return
    if (!target.startsWith("http://") && !target.startsWith("https://")) {
      target = if (target.contains(".") && !target.contains(" ")) {
        "https://$target"
      } else {
        "https://www.google.com/search?q=" + URLEncoder.encode(target, "UTF-8")
      }
    }
    urlInput = target
    webViewRef?.loadUrl(target)
    tabs = tabs.map { if (it.id == activeTabId) it.copy(url = target) else it }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF202124) else Color(0xFFF1F3F4))
  ) {
    // Chrome Tab Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) ChromeOSTitlebarDark else Color(0xFFDFE1E5))
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEach { tab ->
        val isActive = tab.id == activeTabId
        Row(
          modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(
              if (isActive) {
                if (isDarkMode) Color(0xFF292A2D) else Color.White
              } else Color.Transparent
            )
            .clickable { activeTabId = tab.id }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = if (isActive) ChromeOSBlue else Color.Gray,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = tab.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isDarkMode) Color.White else Color.Black,
            modifier = Modifier.widthIn(max = 120.dp)
          )
          if (tabs.size > 1) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Tab",
              tint = Color.Gray,
              modifier = Modifier
                .size(12.dp)
                .clickable {
                  val newTabs = tabs.filter { it.id != tab.id }
                  tabs = newTabs
                  if (activeTabId == tab.id) {
                    activeTabId = newTabs.first().id
                  }
                }
            )
          }
        }
      }

      // Add Tab Button
      IconButton(
        onClick = {
          val newId = "tab_${System.currentTimeMillis()}"
          tabs = tabs + ChromeTab(newId, "New Tab", "https://www.google.com")
          activeTabId = newId
        },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "New Tab",
          tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    // Chrome Navigation & Omnibox Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) Color(0xFF292A2D) else Color.White)
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { if (canGoBack) webViewRef?.goBack() },
        enabled = canGoBack,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = if (canGoBack) (if (isDarkMode) Color.White else Color.Black) else Color.Gray,
          modifier = Modifier.size(16.dp)
        )
      }

      IconButton(
        onClick = { if (canGoForward) webViewRef?.goForward() },
        enabled = canGoForward,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = "Forward",
          tint = if (canGoForward) (if (isDarkMode) Color.White else Color.Black) else Color.Gray,
          modifier = Modifier.size(16.dp)
        )
      }

      IconButton(
        onClick = {
          if (isLoading) webViewRef?.stopLoading() else webViewRef?.reload()
        },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
          contentDescription = "Reload",
          tint = if (isDarkMode) Color.White else Color.Black,
          modifier = Modifier.size(16.dp)
        )
      }

      IconButton(
        onClick = { navigateTo("https://www.google.com") },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Home,
          contentDescription = "Home",
          tint = if (isDarkMode) Color.White else Color.Black,
          modifier = Modifier.size(16.dp)
        )
      }

      // Omnibox URL TextField
      Box(
        modifier = Modifier
          .weight(1f)
          .height(34.dp)
          .clip(RoundedCornerShape(17.dp))
          .background(if (isDarkMode) Color(0xFF171717) else Color(0xFFEDF2FA))
          .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = if (urlInput.startsWith("https://")) Icons.Default.Lock else Icons.Default.Search,
            contentDescription = null,
            tint = if (urlInput.startsWith("https://")) ChromeOSBlue else Color.Gray,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          TextField(
            value = urlInput,
            onValueChange = { urlInput = it },
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
              onGo = {
                focusManager.clearFocus()
                navigateTo(urlInput)
              }
            ),
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight(),
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
          )
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp,
              color = ChromeOSBlue
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(4.dp))
      IconButton(
        onClick = { /* Bookmark toggle */ },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Outlined.Star,
          contentDescription = "Bookmark",
          tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    // Bookmarks bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(if (isDarkMode) Color(0xFF292A2D).copy(alpha = 0.7f) else Color(0xFFF1F3F4))
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      listOf(
        "Google" to "https://www.google.com",
        "Wikipedia" to "https://en.wikipedia.org",
        "GitHub" to "https://github.com",
        "DuckDuckGo" to "https://duckduckgo.com"
      ).forEach { (name, link) ->
        AssistChip(
          onClick = { navigateTo(link) },
          label = { Text(name, fontSize = 10.sp) },
          leadingIcon = {
            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(12.dp))
          },
          modifier = Modifier
            .padding(end = 6.dp)
            .height(24.dp)
        )
      }
    }

    // Android WebView Render Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            webViewClient = object : WebViewClient() {
              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                isLoading = true
                url?.let {
                  urlInput = it
                  tabs = tabs.map { t -> if (t.id == activeTabId) t.copy(url = it) else t }
                }
                canGoBack = view?.canGoBack() ?: false
                canGoForward = view?.canGoForward() ?: false
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
                canGoBack = view?.canGoBack() ?: false
                canGoForward = view?.canGoForward() ?: false
                val pageTitle = view?.title ?: "Page"
                tabs = tabs.map { t ->
                  if (t.id == activeTabId) t.copy(title = if (pageTitle.isBlank()) "Tab" else pageTitle) else t
                }
              }
            }

            webChromeClient = object : WebChromeClient() {
              override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrBlank()) {
                  tabs = tabs.map { t -> if (t.id == activeTabId) t.copy(title = title) else t }
                }
              }
            }

            loadUrl(activeTab.url)
            webViewRef = this
          }
        },
        update = { wv ->
          if (wv.url != activeTab.url) {
            wv.loadUrl(activeTab.url)
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}
