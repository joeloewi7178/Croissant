package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.system.SystemDataSource
import com.joeloewi.croissant.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val systemDataSource: SystemDataSource
) : SystemRepository {

    override fun is24HourFormat(): Flow<Boolean> = systemDataSource.is24HourFormat()
}