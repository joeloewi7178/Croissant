package com.joeloewi.croissant.data.repository.system

import kotlinx.coroutines.flow.Flow

interface SystemDataSource {
    fun is24HourFormat(): Flow<Boolean>
}