package com.joeloewi.croissant.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    fun is24HourFormat(): Flow<Boolean>

    suspend fun isDeviceRooted(): Boolean

    suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean>

    suspend fun removeAllCookies(): Result<Boolean>
}