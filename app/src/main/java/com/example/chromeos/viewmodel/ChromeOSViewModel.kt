package com.example.chromeos.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chromeos.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChromeOSViewModel : ViewModel() {

  private val _windows = MutableStateFlow<List<ChromeOSWindow>>(emptyList())
  val windows: StateFlow<List<ChromeOSWindow>> = _windows.asStateFlow()

  private var nextZIndex = 10f

  // Desktop & Launcher states
  private val _isLauncherOpen = MutableStateFlow(false)
  val isLauncherOpen: StateFlow<Boolean> = _isLauncherOpen.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isQuickSettingsOpen = MutableStateFlow(false)
  val isQuickSettingsOpen: StateFlow<Boolean> = _isQuickSettingsOpen.asStateFlow()

  // System Settings
  private val _isDarkMode = MutableStateFlow(true)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  private val _isNightLightOn = MutableStateFlow(false)
  val isNightLightOn: StateFlow<Boolean> = _isNightLightOn.asStateFlow()

  private val _isWifiOn = MutableStateFlow(true)
  val isWifiOn: StateFlow<Boolean> = _isWifiOn.asStateFlow()

  private val _isBluetoothOn = MutableStateFlow(true)
  val isBluetoothOn: StateFlow<Boolean> = _isBluetoothOn.asStateFlow()

  private val _isCrostiniEnabled = MutableStateFlow(true)
  val isCrostiniEnabled: StateFlow<Boolean> = _isCrostiniEnabled.asStateFlow()

  private val _volume = MutableStateFlow(0.75f)
  val volume: StateFlow<Float> = _volume.asStateFlow()

  private val _brightness = MutableStateFlow(0.85f)
  val brightness: StateFlow<Float> = _brightness.asStateFlow()

  private val _shelfPosition = MutableStateFlow(ShelfPosition.BOTTOM)
  val shelfPosition: StateFlow<ShelfPosition> = _shelfPosition.asStateFlow()

  private val _isShelfAutoHide = MutableStateFlow(false)
  val isShelfAutoHide: StateFlow<Boolean> = _isShelfAutoHide.asStateFlow()

  private val _currentWallpaper = MutableStateFlow(ChromeOSWallpaper.RADIANCE)
  val currentWallpaper: StateFlow<ChromeOSWallpaper> = _currentWallpaper.asStateFlow()

  private val _displayScaling = MutableStateFlow(1.0f)
  val displayScaling: StateFlow<Float> = _displayScaling.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<ChromeOSNotification>>(emptyList())
  val notifications: StateFlow<List<ChromeOSNotification>> = _notifications.asStateFlow()

  // Installed Apps catalog
  val allApps = listOf(
    InstalledApp(AppId.CHROME, "Google Chrome", isPinned = true, category = "Google", description = "Fast, secure web browser for ChromeOS"),
    InstalledApp(AppId.FILES, "Files", isPinned = true, category = "System", description = "Explore Downloads, Google Drive, and Linux files"),
    InstalledApp(AppId.TERMINAL, "Terminal (Linux)", isPinned = true, category = "Development", description = "Crostini Debian Linux subsystem & shell"),
    InstalledApp(AppId.SETTINGS, "Settings", isPinned = true, category = "System", description = "Configure network, display, wallpapers, and host info"),
    InstalledApp(AppId.WEB_STORE, "Play / Web Store", isPinned = true, category = "Google", description = "Discover and install ChromeOS apps"),
    InstalledApp(AppId.CALCULATOR, "Calculator", isPinned = false, category = "Productivity", description = "ChromeOS scientific calculator"),
    InstalledApp(AppId.TEXT_EDITOR, "Text Editor", isPinned = false, category = "Productivity", description = "Minimalist text & code editor"),
    InstalledApp(AppId.CANVAS, "Chrome Canvas", isPinned = false, category = "Creativity", description = "Hand-draw, sketch, and export artwork"),
    InstalledApp(AppId.TASK_MANAGER, "Task Manager", isPinned = false, category = "System", description = "Monitor CPU, RAM, and terminate processes"),
    InstalledApp(AppId.DIAGNOSTICS, "System Diagnostics", isPinned = false, category = "System", description = "Android 7.0+ compatibility & hardware specs")
  )

  // Crostini Terminal State
  private val _terminalLines = MutableStateFlow<List<TerminalLine>>(emptyList())
  val terminalLines: StateFlow<List<TerminalLine>> = _terminalLines.asStateFlow()

  private val _terminalCurrentDir = MutableStateFlow("~/")
  val terminalCurrentDir: StateFlow<String> = _terminalCurrentDir.asStateFlow()

  // Virtual Filesystem State
  private val _files = MutableStateFlow<List<VFile>>(emptyList())
  val files: StateFlow<List<VFile>> = _files.asStateFlow()

  // Host Device Info
  private val _hostInfo = MutableStateFlow<HostDeviceInfo?>(null)
  val hostInfo: StateFlow<HostDeviceInfo?> = _hostInfo.asStateFlow()

  init {
    initDefaultFiles()
    initCrostiniTerminal()
    addInitialNotification()
  }

  fun initHostInfo(context: Context) {
    try {
      val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
      val memInfo = ActivityManager.MemoryInfo()
      actManager?.getMemoryInfo(memInfo)

      val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
      val batteryPct = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 85
      val isCharging = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val status = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
      } else {
        false
      }

      val totalMb = memInfo.totalMem / (1024 * 1024)
      val availMb = memInfo.availMem / (1024 * 1024)

      val info = HostDeviceInfo(
        osVersion = Build.VERSION.RELEASE ?: "7.0",
        apiLevel = Build.VERSION.SDK_INT,
        deviceModel = Build.MODEL ?: "Generic Android Device",
        manufacturer = Build.MANUFACTURER ?: "Android",
        cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
        totalRamMb = totalMb,
        availableRamMb = availMb,
        batteryPct = batteryPct,
        isCharging = isCharging
      )
      _hostInfo.value = info
    } catch (_: Exception) {
      _hostInfo.value = HostDeviceInfo(
        osVersion = "7.0 (Nougat+)",
        apiLevel = 24,
        deviceModel = "Android Emulator",
        manufacturer = "Google",
        cpuAbi = "arm64-v8a",
        totalRamMb = 4096,
        availableRamMb = 2150,
        batteryPct = 90,
        isCharging = true
      )
    }
  }

  private fun addInitialNotification() {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    _notifications.value = listOf(
      ChromeOSNotification(
        id = "notif_welcome",
        title = "ChromeOS Ready",
        message = "Aura Window Manager active. Android 7.0+ (API 24+) host verified.",
        timestamp = time,
        iconAppId = AppId.SETTINGS
      )
    )
  }

  // --- WINDOW MANAGEMENT ---

  fun openApp(appId: AppId, payload: String? = null) {
    closeLauncher()
    closeQuickSettings()

    val currentList = _windows.value
    val existing = currentList.find { it.appId == appId }

    if (existing != null) {
      // Bring existing window to front & unminimize
      nextZIndex += 1f
      _windows.value = currentList.map {
        if (it.id == existing.id) {
          it.copy(isMinimized = false, zIndex = nextZIndex, extraPayload = payload ?: it.extraPayload)
        } else it
      }
      return
    }

    // Default window dimensions based on app type
    val defaultWidth: Dp
    val defaultHeight: Dp
    when (appId) {
      AppId.CHROME -> {
        defaultWidth = 520.dp
        defaultHeight = 440.dp
      }
      AppId.TERMINAL -> {
        defaultWidth = 460.dp
        defaultHeight = 350.dp
      }
      AppId.SETTINGS, AppId.FILES -> {
        defaultWidth = 480.dp
        defaultHeight = 380.dp
      }
      AppId.CALCULATOR -> {
        defaultWidth = 320.dp
        defaultHeight = 420.dp
      }
      AppId.CANVAS, AppId.TASK_MANAGER -> {
        defaultWidth = 460.dp
        defaultHeight = 380.dp
      }
      else -> {
        defaultWidth = 420.dp
        defaultHeight = 360.dp
      }
    }

    // Calculate staggered offset
    val count = currentList.size
    val offsetX = 30f + (count % 5) * 35f
    val offsetY = 30f + (count % 5) * 30f

    nextZIndex += 1f
    val newWindow = ChromeOSWindow(
      id = "win_${appId.idStr}_${System.currentTimeMillis()}",
      appId = appId,
      title = appId.defaultTitle,
      position = Offset(offsetX, offsetY),
      size = Pair(defaultWidth, defaultHeight),
      isMinimized = false,
      zIndex = nextZIndex,
      extraPayload = payload
    )

    _windows.value = currentList + newWindow
  }

  fun closeWindow(windowId: String) {
    _windows.value = _windows.value.filter { it.id != windowId }
  }

  fun minimizeWindow(windowId: String) {
    _windows.value = _windows.value.map {
      if (it.id == windowId) it.copy(isMinimized = true) else it
    }
  }

  fun toggleMaximizeWindow(windowId: String) {
    _windows.value = _windows.value.map {
      if (it.id == windowId) {
        val nextState = if (it.snapState == WindowSnapState.MAXIMIZED) {
          WindowSnapState.NONE
        } else {
          WindowSnapState.MAXIMIZED
        }
        it.copy(snapState = nextState, isMinimized = false)
      } else it
    }
  }

  fun snapWindow(windowId: String, snapState: WindowSnapState) {
    _windows.value = _windows.value.map {
      if (it.id == windowId) {
        it.copy(snapState = snapState, isMinimized = false)
      } else it
    }
  }

  fun bringToFront(windowId: String) {
    nextZIndex += 1f
    _windows.value = _windows.value.map {
      if (it.id == windowId) it.copy(zIndex = nextZIndex, isMinimized = false) else it
    }
  }

  fun updateWindowPosition(windowId: String, delta: Offset) {
    _windows.value = _windows.value.map {
      if (it.id == windowId) {
        val newX = (it.position.x + delta.x).coerceAtLeast(0f)
        val newY = (it.position.y + delta.y).coerceAtLeast(0f)
        it.copy(position = Offset(newX, newY), snapState = WindowSnapState.NONE)
      } else it
    }
  }

  fun updateWindowSize(windowId: String, deltaWidth: Dp, deltaHeight: Dp) {
    _windows.value = _windows.value.map {
      if (it.id == windowId) {
        val currentW = it.size.first
        val currentH = it.size.second
        val newW = (currentW + deltaWidth).coerceIn(280.dp, 1000.dp)
        val newH = (currentH + deltaHeight).coerceIn(200.dp, 800.dp)
        it.copy(size = Pair(newW, newH), snapState = WindowSnapState.NONE)
      } else it
    }
  }

  // --- LAUNCHER & QUICK SETTINGS ---

  fun toggleLauncher() {
    _isLauncherOpen.value = !_isLauncherOpen.value
    if (_isLauncherOpen.value) {
      _isQuickSettingsOpen.value = false
    }
  }

  fun closeLauncher() {
    _isLauncherOpen.value = false
    _searchQuery.value = ""
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleQuickSettings() {
    _isQuickSettingsOpen.value = !_isQuickSettingsOpen.value
    if (_isQuickSettingsOpen.value) {
      _isLauncherOpen.value = false
    }
  }

  fun closeQuickSettings() {
    _isQuickSettingsOpen.value = false
  }

  // --- SETTINGS CONTROLS ---

  fun toggleDarkMode() {
    _isDarkMode.value = !_isDarkMode.value
  }

  fun toggleNightLight() {
    _isNightLightOn.value = !_isNightLightOn.value
  }

  fun toggleWifi() {
    _isWifiOn.value = !_isWifiOn.value
  }

  fun toggleBluetooth() {
    _isBluetoothOn.value = !_isBluetoothOn.value
  }

  fun setVolume(vol: Float) {
    _volume.value = vol.coerceIn(0f, 1f)
  }

  fun setBrightness(b: Float) {
    _brightness.value = b.coerceIn(0.1f, 1f)
  }

  fun setShelfPosition(pos: ShelfPosition) {
    _shelfPosition.value = pos
  }

  fun toggleShelfAutoHide() {
    _isShelfAutoHide.value = !_isShelfAutoHide.value
  }

  fun setWallpaper(wp: ChromeOSWallpaper) {
    _currentWallpaper.value = wp
  }

  fun setDisplayScaling(scale: Float) {
    _displayScaling.value = scale
  }

  fun clearNotification(id: String) {
    _notifications.value = _notifications.value.filter { it.id != id }
  }

  fun clearAllNotifications() {
    _notifications.value = emptyList()
  }

  // --- CROSTINI LINUX SHELL ENGINE ---

  private fun initCrostiniTerminal() {
    _terminalLines.value = listOf(
      TerminalLine("Linux container crostini (Debian bookworm) ready.", LineType.SYSTEM),
      TerminalLine("Type 'help' to see available Linux & ChromeOS commands.", LineType.INFO),
      TerminalLine("Type 'neofetch' or 'chromeos-fetch' for system specs.", LineType.INFO)
    )
  }

  fun executeTerminalCommand(input: String) {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return

    val current = _terminalLines.value.toMutableList()
    val promptPrefix = "penguin@crostini:${_terminalCurrentDir.value}$ "
    current.add(TerminalLine(promptPrefix + trimmed, LineType.COMMAND))

    val parts = trimmed.split("\\s+".toRegex())
    val cmd = parts.firstOrNull()?.lowercase() ?: ""
    val args = parts.drop(1)

    when (cmd) {
      "help" -> {
        current.add(TerminalLine("Crostini Linux Subsystem v5.15 Commands:", LineType.INFO))
        current.add(TerminalLine("  neofetch / chromeos-fetch - Display ChromeOS & Crostini specs", LineType.OUTPUT))
        current.add(TerminalLine("  uname -a                  - Print kernel & architecture details", LineType.OUTPUT))
        current.add(TerminalLine("  ls [-la]                  - List files in current directory", LineType.OUTPUT))
        current.add(TerminalLine("  cat <file>                - View file content", LineType.OUTPUT))
        current.add(TerminalLine("  mkdir <name>              - Create directory", LineType.OUTPUT))
        current.add(TerminalLine("  touch <file>              - Create empty file", LineType.OUTPUT))
        current.add(TerminalLine("  pwd                       - Print working directory", LineType.OUTPUT))
        current.add(TerminalLine("  top / ps                  - Process activity", LineType.OUTPUT))
        current.add(TerminalLine("  free -m                   - RAM utilization in MB", LineType.OUTPUT))
        current.add(TerminalLine("  date / uptime / whoami    - System status utilities", LineType.OUTPUT))
        current.add(TerminalLine("  echo <text>               - Print text to stdout", LineType.OUTPUT))
        current.add(TerminalLine("  python3 <expr>            - Evaluate Python expression (e.g. 2**10)", LineType.OUTPUT))
        current.add(TerminalLine("  apt-get install <pkg>     - Simulated Debian package installation", LineType.OUTPUT))
        current.add(TerminalLine("  clear                     - Clear terminal buffer", LineType.OUTPUT))
      }

      "neofetch", "chromeos-fetch" -> {
        val host = _hostInfo.value
        val mem = host?.let { "${it.totalRamMb - it.availableRamMb}MiB / ${it.totalRamMb}MiB" } ?: "1850MiB / 4096MiB"
        val ascii = listOf(
          "       .---.       penguin@crostini",
          "      /     \\      ----------------",
          "     | () () |     OS: Google ChromeOS 130.0 (Official Build)",
          "      \\  _  /      Subsystem: Crostini Linux (Debian 12 / Bookworm)",
          "       /   \\       Host Android: ${host?.osVersion ?: "7.0+"} (API ${host?.apiLevel ?: 24})",
          "      /|   |\\      Device: ${host?.manufacturer ?: "Android"} ${host?.deviceModel ?: "Device"}",
          "     / |   | \\     Kernel: Linux 5.15.0-crostini-android7+",
          "    /  |   |  \\    Uptime: 2 hours, 18 mins",
          "   (___|___|___)   Shell: bash 5.2.15",
          "                   Memory: $mem",
          "                   WM: ChromeOS Ash/Aura (Wayland)"
        )
        ascii.forEach { current.add(TerminalLine(it, LineType.ASCII)) }
      }

      "uname" -> {
        if (args.contains("-a")) {
          val abi = _hostInfo.value?.cpuAbi ?: "aarch64"
          current.add(TerminalLine("Linux crostini 5.15.137-chromeos-android7+ #1 SMP PREEMPT 2026 $abi GNU/Linux", LineType.OUTPUT))
        } else {
          current.add(TerminalLine("Linux", LineType.OUTPUT))
        }
      }

      "ls" -> {
        val showAll = args.contains("-la") || args.contains("-a")
        val vfiles = _files.value.filter { it.category == "Linux files" }
        if (showAll) {
          current.add(TerminalLine("total 24", LineType.INFO))
          current.add(TerminalLine("drwxr-xr-x 4 penguin penguin 4096 Nov 8 23:40 .", LineType.OUTPUT))
          current.add(TerminalLine("drwxr-xr-x 3 root    root    4096 Nov 8 23:40 ..", LineType.OUTPUT))
          current.add(TerminalLine("-rw-r--r-- 1 penguin penguin  220 Nov 8 23:40 .bash_logout", LineType.OUTPUT))
          current.add(TerminalLine("-rw-r--r-- 1 penguin penguin 3526 Nov 8 23:40 .bashrc", LineType.OUTPUT))
          current.add(TerminalLine("-rw-r--r-- 1 penguin penguin  807 Nov 8 23:40 .profile", LineType.OUTPUT))
        }
        vfiles.forEach {
          val prefix = if (it.isFolder) "drwxr-xr-x" else "-rw-r--r--"
          if (showAll) {
            current.add(TerminalLine("$prefix 1 penguin penguin ${it.sizeBytes} ${it.modified} ${it.name}", LineType.OUTPUT))
          } else {
            current.add(TerminalLine(it.name + if (it.isFolder) "/" else "", if (it.isFolder) LineType.INFO else LineType.OUTPUT))
          }
        }
        if (vfiles.isEmpty() && !showAll) {
          current.add(TerminalLine("(empty directory)", LineType.INFO))
        }
      }

      "pwd" -> {
        current.add(TerminalLine("/home/penguin", LineType.OUTPUT))
      }

      "whoami" -> current.add(TerminalLine("penguin", LineType.OUTPUT))
      "hostname" -> current.add(TerminalLine("crostini", LineType.OUTPUT))

      "uptime" -> {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        current.add(TerminalLine(" $time up 2:18,  1 user,  load average: 0.12, 0.08, 0.05", LineType.OUTPUT))
      }

      "date" -> {
        current.add(TerminalLine(Date().toString(), LineType.OUTPUT))
      }

      "free" -> {
        val host = _hostInfo.value
        val total = host?.totalRamMb ?: 4096
        val avail = host?.availableRamMb ?: 2200
        val used = total - avail
        current.add(TerminalLine("              total        used        free      shared  buff/cache   available", LineType.INFO))
        current.add(TerminalLine("Mem:         ${total}M       ${used}M       ${avail}M         12M        480M       ${avail}M", LineType.OUTPUT))
        current.add(TerminalLine("Swap:        2048M          0M       2048M", LineType.OUTPUT))
      }

      "top", "ps" -> {
        current.add(TerminalLine("  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND", LineType.INFO))
        current.add(TerminalLine("    1 root      20   0  168540  11420   8324 S   0.0   0.3   0:01.42 systemd", LineType.OUTPUT))
        current.add(TerminalLine("  124 chronos   20   0  984210  94120  42100 S   1.8   2.3   0:08.55 chrome-ash", LineType.OUTPUT))
        current.add(TerminalLine("  312 penguin   20   0   14320   4210   3120 S   0.2   0.1   0:00.31 bash", LineType.OUTPUT))
        current.add(TerminalLine("  450 penguin   20   0   28500   5120   3840 R   0.5   0.1   0:00.04 top", LineType.OUTPUT))
      }

      "cat" -> {
        val fileName = args.firstOrNull()
        if (fileName == null) {
          current.add(TerminalLine("cat: missing operand", LineType.ERROR))
        } else {
          val file = _files.value.find { it.name.equals(fileName, ignoreCase = true) }
          if (file != null) {
            current.add(TerminalLine(file.content, LineType.OUTPUT))
          } else {
            current.add(TerminalLine("cat: $fileName: No such file or directory", LineType.ERROR))
          }
        }
      }

      "mkdir" -> {
        val folderName = args.firstOrNull()
        if (folderName == null) {
          current.add(TerminalLine("mkdir: missing operand", LineType.ERROR))
        } else {
          addFile(VFile(
            id = "file_${System.currentTimeMillis()}",
            name = folderName,
            path = "/home/penguin/$folderName",
            isFolder = true,
            sizeBytes = 4096,
            category = "Linux files"
          ))
          current.add(TerminalLine("Directory '$folderName' created.", LineType.SUCCESS))
        }
      }

      "touch" -> {
        val fileName = args.firstOrNull()
        if (fileName == null) {
          current.add(TerminalLine("touch: missing file operand", LineType.ERROR))
        } else {
          addFile(VFile(
            id = "file_${System.currentTimeMillis()}",
            name = fileName,
            path = "/home/penguin/$fileName",
            isFolder = false,
            content = "# Created via touch in Crostini\n",
            sizeBytes = 32,
            category = "Linux files"
          ))
          current.add(TerminalLine("File '$fileName' created.", LineType.SUCCESS))
        }
      }

      "echo" -> {
        current.add(TerminalLine(args.joinToString(" "), LineType.OUTPUT))
      }

      "clear" -> {
        _terminalLines.value = emptyList()
        return
      }

      "apt-get", "apt" -> {
        val subcmd = args.firstOrNull()
        val pkg = args.drop(1).firstOrNull() ?: "build-essential"
        if (subcmd == "install") {
          current.add(TerminalLine("Reading package lists... Done", LineType.OUTPUT))
          current.add(TerminalLine("Building dependency tree... Done", LineType.OUTPUT))
          current.add(TerminalLine("The following NEW packages will be installed: $pkg", LineType.INFO))
          current.add(TerminalLine("Fetched 2,410 kB in 1s (2,410 kB/s)", LineType.OUTPUT))
          current.add(TerminalLine("Setting up $pkg (1.0-debian)...", LineType.OUTPUT))
          current.add(TerminalLine("Installation of '$pkg' successful!", LineType.SUCCESS))
        } else {
          current.add(TerminalLine("Usage: apt-get install <package_name>", LineType.INFO))
        }
      }

      "python", "python3" -> {
        if (args.isEmpty()) {
          current.add(TerminalLine("Python 3.11.2 (main, Debian 12)", LineType.INFO))
          current.add(TerminalLine("Type 'python3 <math_expression>' to evaluate (e.g. python3 2**10)", LineType.OUTPUT))
        } else {
          val expr = args.joinToString(" ")
          val res = tryEvaluateSimpleMath(expr)
          current.add(TerminalLine(">>> $expr", LineType.INFO))
          current.add(TerminalLine(res, LineType.OUTPUT))
        }
      }

      else -> {
        current.add(TerminalLine("bash: $cmd: command not found. Type 'help' for available commands.", LineType.ERROR))
      }
    }

    _terminalLines.value = current
  }

  private fun tryEvaluateSimpleMath(expr: String): String {
    return try {
      val clean = expr.replace(" ", "")
      when {
        clean.contains("+") -> {
          val p = clean.split("+")
          (p[0].toDouble() + p[1].toDouble()).toString()
        }
        clean.contains("-") && !clean.startsWith("-") -> {
          val p = clean.split("-")
          (p[0].toDouble() - p[1].toDouble()).toString()
        }
        clean.contains("**") -> {
          val p = clean.split("**")
          Math.pow(p[0].toDouble(), p[1].toDouble()).toLong().toString()
        }
        clean.contains("*") -> {
          val p = clean.split("*")
          (p[0].toDouble() * p[1].toDouble()).toString()
        }
        clean.contains("/") -> {
          val p = clean.split("/")
          (p[0].toDouble() / p[1].toDouble()).toString()
        }
        else -> "Output: $expr"
      }
    } catch (_: Exception) {
      "Output: $expr"
    }
  }

  // --- VIRTUAL FILESYSTEM ---

  private fun initDefaultFiles() {
    _files.value = listOf(
      VFile("f1", "Welcome_ChromeOS.txt", "/My files/Downloads/Welcome_ChromeOS.txt", false,
        "Welcome to Google ChromeOS!\n\nThis emulator provides a full desktop experience on Android 7.0+ (API 24+).\nFeatures:\n- Aura Window Manager with multi-window multitasking\n- Full Chromium-powered Web Browser\n- Crostini Linux container terminal\n- Files app and Settings app\n- Chrome Web Store & PWA support\n- Customizable wallpapers & themes",
        1024, "Today, 10:00 AM", "Downloads"),
      VFile("f2", "Android_7_Compatibility_Guide.md", "/My files/Downloads/Android_7_Compatibility_Guide.md", false,
        "# Android 7.0+ (Nougat) Compatibility Report\n\n- Min SDK: 24 (Android 7.0)\n- Target SDK: 36 (Android 16)\n- UI Engine: Jetpack Compose Material 3\n- Architecture: ARM64 / x86_64\n- Virtual Display: DPI-scalable Aura Canvas\n- Status: 100% Certified Native Execution\n",
        2048, "Today, 10:15 AM", "Downloads"),
      VFile("f3", "project_notes.txt", "/My files/Google Drive/project_notes.txt", false,
        "ChromeOS on Android:\n1. Open Chrome to browse docs\n2. Open Terminal to test python/bash commands\n3. Snap windows side-by-side for desktop productivity",
        512, "Yesterday, 4:30 PM", "Google Drive"),
      VFile("f4", "hello_world.py", "/home/penguin/hello_world.py", false,
        "print('Hello from Crostini Linux on Android 7+!')\n",
        64, "Today, 10:30 AM", "Linux files"),
      VFile("f5", "Projects", "/My files/Downloads/Projects", true, "", 4096, "Today, 10:00 AM", "Downloads")
    )
  }

  fun addFile(file: VFile) {
    _files.value = _files.value + file
  }

  fun deleteFile(fileId: String) {
    _files.value = _files.value.filter { it.id != fileId }
  }

  fun saveFileContent(fileId: String, newContent: String) {
    _files.value = _files.value.map {
      if (it.id == fileId) it.copy(content = newContent, sizeBytes = newContent.toByteArray().size.toLong()) else it
    }
  }

  // --- TASK MANAGER CONTROLS ---

  fun killProcessByAppId(appId: AppId) {
    _windows.value = _windows.value.filter { it.appId != appId }
  }
}
