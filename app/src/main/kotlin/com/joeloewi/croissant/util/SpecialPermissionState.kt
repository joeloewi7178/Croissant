package com.joeloewi.croissant.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

enum class SpecialPermission {
    ScheduleExactAlarms {
        override fun getIntentForRequest(context: Context): Intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            } else {
                //this intent won't be launched
                Intent()
            }

        override fun isPermitted(context: Context): Boolean =
            context.getSystemService<AlarmManager>()!!.canScheduleExactAlarmsCompat()
    },

    @SuppressLint("BatteryLife")
    IgnoreBatteryOptimization {
        override fun getIntentForRequest(context: Context): Intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val withPackage =
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                val withoutPackage =
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

                listOf(
                    withPackage,
                    withoutPackage
                ).find { it.resolveActivity(context.packageManager) != null }!!
            } else {
                //this intent won't be launched
                Intent()
            }

        override fun isPermitted(context: Context): Boolean =
            context.getSystemService<PowerManager>()!!.isIgnoringBatteryOptimizationsCompat(context)
    };

    abstract fun getIntentForRequest(context: Context): Intent
    abstract fun isPermitted(context: Context): Boolean
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberSpecialPermissionState(
    specialPermission: SpecialPermission,
    onPermissionResult: (Boolean) -> Unit = {}
): SpecialPermissionState {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val permissionState = remember(specialPermission) {
        SpecialPermissionState(specialPermission.name, context)
    }
    // Refresh the permission status when the lifecycle is resumed
    val permissionCheckerObserver = remember(permissionState) {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // If the permission is revoked, check again.
                // We don't check if the permission was denied as that triggers a process restart.
                if (permissionState.status != PermissionStatus.Granted) {
                    permissionState.refreshPermissionStatus()
                }
            }
        }
    }

    DisposableEffect(lifecycle, permissionCheckerObserver) {
        lifecycle.addObserver(permissionCheckerObserver)
        onDispose { lifecycle.removeObserver(permissionCheckerObserver) }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            permissionState.refreshPermissionStatus()
            onPermissionResult(specialPermission.isPermitted(context))
        }

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

@OptIn(ExperimentalPermissionsApi::class)
@Stable
class SpecialPermissionState(
    override val permission: String,
    private val context: Context
) : PermissionState {
    private val _specialPermission = SpecialPermission.valueOf(permission)

    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    override fun launchPermissionRequest() {
        launcher?.launch(_specialPermission.getIntentForRequest(context))
            ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    internal var launcher: ActivityResultLauncher<Intent>? = null

    private fun getPermissionStatus(): PermissionStatus =
        if (_specialPermission.isPermitted(context)) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(false)
        }
}