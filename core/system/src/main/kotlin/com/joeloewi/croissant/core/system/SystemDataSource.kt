package com.joeloewi.croissant.core.system

import kotlinx.coroutines.flow.Flow

interface SystemDataSource {
    fun is24HourFormat(): Flow<Boolean>

    suspend fun isDeviceRooted(): Boolean

    suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean>

    suspend fun removeAllCookies(): Result<Boolean>

    suspend fun checkPermissions(vararg permissions: String): List<Pair<String, Boolean>>
}