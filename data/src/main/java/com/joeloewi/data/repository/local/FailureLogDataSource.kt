package com.joeloewi.data.repository.local

import com.joeloewi.domain.entity.FailureLog

interface FailureLogDataSource {
    suspend fun insert(failureLog: FailureLog): Long
}