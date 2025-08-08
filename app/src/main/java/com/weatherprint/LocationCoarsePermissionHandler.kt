/**
 * ------------------------------------------------------------
 * ACCESS_COARSE_LOCATION 権限チェック
 * ------------------------------------------------------------
 */
package com.weatherprint

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class LocationCoarsePermissionHandler() {
    // No Constructor
}

/**
 * ACCESS_COARSE_LOCATION 権限取得結果
 */
@Composable
fun LocationCoarsePermission() {
    val context = LocalContext.current
    val isGranted = remember { mutableStateOf(false) }

    LocationCoarseCheck { granted ->
        isGranted.value = granted
    }

    if (!isGranted.value) {
        showToastLong(context, "Bluetoothの利用権限を許可してください")
    }
}

/**
 * ACCESS_COARSE_LOCATION 権限許可表示
 */
@Composable
fun LocationCoarseCheck(onGranted: (Boolean) -> Unit) {
    val launcher = rememberLauncherForActivityResult (
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onGranted(granted)
    }

    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onGranted(true)
    } else {
        SideEffect {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}





