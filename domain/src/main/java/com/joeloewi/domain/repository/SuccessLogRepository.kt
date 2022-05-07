package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.SuccessLog

interface SuccessLogRepository {
    suspend fun insert(successLog: SuccessLog): Long
}