package com.joeloewi.croissant.domain.repository

import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    fun is24HourFormat(): Flow<Boolean>
}