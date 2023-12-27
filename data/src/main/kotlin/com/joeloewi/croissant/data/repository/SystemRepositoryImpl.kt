package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.system.SystemDataSource
import com.joeloewi.croissant.domain.repository.SystemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val systemDataSource: SystemDataSource
) : SystemRepository {

    override fun is24HourFormat(): Flow<Boolean> = systemDataSource.is24HourFormat()

    override suspend fun isDeviceRooted(): Boolean = withContext(Dispatchers.IO) {
        systemDataSource.isDeviceRooted()
    }

    override suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean> =
        withContext(Dispatchers.IO) {
            systemDataSource.isUnusedAppRestrictionEnabled()
        }
}