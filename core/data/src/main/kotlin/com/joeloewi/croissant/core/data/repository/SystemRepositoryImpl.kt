package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.system.SystemDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val systemDataSource: SystemDataSource
) : SystemRepository {

    override fun is24HourFormat(): Flow<Boolean> = systemDataSource.is24HourFormat()

    override suspend fun isDeviceRooted(): Boolean = systemDataSource.isDeviceRooted()

    override suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean> =
        systemDataSource.isUnusedAppRestrictionEnabled()

    override suspend fun removeAllCookies(): Result<Boolean> = systemDataSource.removeAllCookies()
}