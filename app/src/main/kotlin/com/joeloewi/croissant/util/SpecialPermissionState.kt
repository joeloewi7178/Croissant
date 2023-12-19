package com.joeloewi.croissant.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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

enum class SpecialPermission(
    val intent: Intent
) {
    ScheduleExactAlarms(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        } else {
            //this intent won't be launched
            Intent()
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberSpecialPermissionState(
    specialPermission: SpecialPermission,
    intentForRequestPermission: Intent = specialPermission.intent,
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
                val hasPermission = when (specialPermission) {
                    SpecialPermission.ScheduleExactAlarms -> {
                        context.getSystemService<AlarmManager>()!!.canScheduleExactAlarmsCompat()
                    }
                }

                onPermissionResult(hasPermission)
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

    DisposableEffect(permissionState, intentForRequestPermission) {
        permissionState.intent = intentForRequestPermission
        onDispose {
            permissionState.intent = null
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

    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    override fun launchPermissionRequest() {
        val tempIntent = intent
        if (tempIntent != null) {
            if (tempIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(tempIntent)
            }
            return
        }
    }

    internal var intent: Intent? = null

    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = when (SpecialPermission.valueOf(permission)) {
            SpecialPermission.ScheduleExactAlarms -> {
                context.getSystemService<AlarmManager>()!!.canScheduleExactAlarmsCompat()
            }
        }

        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(false)
        }
    }
}