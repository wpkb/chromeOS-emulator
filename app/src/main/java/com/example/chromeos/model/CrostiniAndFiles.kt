package com.example.chromeos.model

enum class LineType {
  COMMAND,
  OUTPUT,
  ERROR,
  SUCCESS,
  SYSTEM,
  INFO,
  ASCII
}

data class TerminalLine(
  val text: String,
  val type: LineType = LineType.OUTPUT
)

data class VFile(
  val id: String,
  val name: String,
  val path: String,
  val isFolder: Boolean,
  val content: String = "",
  val sizeBytes: Long = 1024,
  val modified: String = "Today, 10:24 AM",
  val category: String = "Downloads" // Downloads, Google Drive, Linux files
)

data class HostDeviceInfo(
  val osVersion: String,
  val apiLevel: Int,
  val deviceModel: String,
  val manufacturer: String,
  val cpuAbi: String,
  val totalRamMb: Long,
  val availableRamMb: Long,
  val batteryPct: Int,
  val isCharging: Boolean,
  val chromeOSVersion: String = "130.0.6723.102 (Official Build) 64-bit",
  val crostiniStatus: String = "Crostini Linux Container v5.15 (penguin) Running"
)
