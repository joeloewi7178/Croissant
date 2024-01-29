package com.joeloewi.croissant.data.repository.system

import kotlinx.coroutines.flow.Flow

interface SystemDataSource {
    fun is24HourFormat(): Flow<Boolean>

    suspend fun isDeviceRooted(): Boolean

    suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean>

    suspend fun removeAllCookies(): Result<Boolean>

    suspend fun isNetworkAvailable(): Boolean

    suspend fun isNetworkVpn(): Boolean
}