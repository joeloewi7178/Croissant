package com.joeloewi.data.repository.local

import com.joeloewi.domain.entity.SuccessLog

interface SuccessLogDataSource {
    suspend fun insert(successLog: SuccessLog): Long
}