package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.chromeos.model.AppId
import com.example.chromeos.viewmodel.ChromeOSViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ChromeOS Emulator", appName)
  }

  @Test
  fun `chromeos viewmodel app lifecycle`() {
    val vm = ChromeOSViewModel()
    assertEquals(0, vm.windows.value.size)

    vm.openApp(AppId.CHROME)
    assertEquals(1, vm.windows.value.size)
    assertEquals(AppId.CHROME, vm.windows.value.first().appId)

    vm.openApp(AppId.TERMINAL)
    assertEquals(2, vm.windows.value.size)

    val chromeWin = vm.windows.value.first { it.appId == AppId.CHROME }
    vm.closeWindow(chromeWin.id)
    assertEquals(1, vm.windows.value.size)
    assertEquals(AppId.TERMINAL, vm.windows.value.first().appId)
  }

  @Test
  fun `crostini terminal command execution`() {
    val vm = ChromeOSViewModel()
    val initialCount = vm.terminalLines.value.size

    vm.executeTerminalCommand("uname -a")
    assertTrue(vm.terminalLines.value.size > initialCount)

    vm.executeTerminalCommand("clear")
    assertEquals(0, vm.terminalLines.value.size)
  }
}
