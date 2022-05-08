package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.FailureLog

interface FailureLogRepository {
    suspend fun insert(failureLog: FailureLog): Long
}