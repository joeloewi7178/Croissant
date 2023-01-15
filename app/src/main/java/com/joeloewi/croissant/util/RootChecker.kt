package com.joeloewi.croissant.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset

class RootChecker(
    private val context: Context
) {

    private val rootFiles = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/system/usr/we-need-root/",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/su/bin",
        "/system/xbin/daemonsu"
    )

    private val rootPackages = arrayOf(
        "com.devadvance.rootcloak",
        "com.devadvance.rootcloakplus",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "de.robv.android.xposed.installer",
        "com.saurik.substrate",
        "com.zachspong.temprootremovejb",
        "com.amphoras.hidemyroot",
        "com.amphoras.hidemyrootadfree",
        "com.formyhm.hiderootPremium",
        "com.formyhm.hideroot",
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "com.yellowes.su",
        "com.topjohnwu.magisk",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global",
        "com.alephzain.framaroot"
    )

    private val runtime by lazy {
        Runtime.getRuntime()
    }

    suspend fun isDeviceRooted(): Boolean =
        checkRootFiles() || checkSUExist() || checkRootPackages()

    private suspend fun checkRootFiles(): Boolean = withContext(Dispatchers.IO) {
        rootFiles.runCatching {
            any { path -> File(path).exists() }
        }.fold(
            onSuccess = {
                it
            },
            onFailure = {
                false
            }
        )
    }

    private suspend fun checkSUExist(): Boolean = withContext(Dispatchers.IO) {
        var process: Process? = null

        runtime.runCatching {
            exec(arrayOf("/system/xbin/which", "su"))
        }.mapCatching {
            process = it
            it.inputStream.bufferedReader(Charset.forName("UTF-8"))
                .use { reader -> reader.readLine() } != null
        }.fold(
            onSuccess = {
                it
            },
            onFailure = {
                false
            }
        ).also {
            process?.destroy()
        }
    }

    private suspend fun checkRootPackages(): Boolean = withContext(Dispatchers.Default) {
        context.runCatching {
            packageManager
        }.mapCatching {
            for (pkg in rootPackages) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
                } else {
                    it.getPackageInfo(pkg, 0)
                }
            }
        }.fold(
            onSuccess = {
                true
            },
            onFailure = {
                false
            }
        )
    }
}