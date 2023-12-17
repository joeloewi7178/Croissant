package com.joeloewi.croissant.util

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.AppOpsManagerCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberSpecialPermissionState(
    permission: String,
    intentForRequestPermission: Intent,
    onPermissionResult: (Boolean) -> Unit = {}
): SpecialPermissionState {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val permissionState = remember(permission) {
        SpecialPermissionState(permission, context)
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

    DisposableEffect(Unit) {
        val appOpsManager: AppOpsManager? = context.getSystemService()

        val opChangedListener = AppOpsManager.OnOpChangedListener { op, packageName ->
            if (op == permission && packageName == context.packageName) {
                permissionState.refreshPermissionStatus()
                onPermissionResult(
                    AppOpsManagerCompat.checkOrNoteProxyOp(
                        context,
                        context.applicationInfo.uid,
                        op,
                        context.packageName
                    ) == AppOpsManager.MODE_ALLOWED
                )
            }
        }

        appOpsManager?.startWatchingMode(permission, context.packageName, opChangedListener)

        onDispose { appOpsManager?.stopWatchingMode(opChangedListener) }
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
        val hasPermission = AppOpsManagerCompat.checkOrNoteProxyOp(
            context,
            context.applicationInfo.uid,
            permission,
            context.packageName
        ) == AppOpsManager.MODE_ALLOWED

        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(false)
        }
    }
}