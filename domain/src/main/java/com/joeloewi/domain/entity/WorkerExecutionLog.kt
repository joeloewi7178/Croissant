package com.joeloewi.domain.entity

import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import java.util.*

data class WorkerExecutionLog(
    val id: Long = 0,
    val attendanceId: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val timezoneId: String = TimeZone.getDefault().id,
    val state: WorkerExecutionLogState = WorkerExecutionLogState.SUCCESS,
    val loggableWorker: LoggableWorker = LoggableWorker.UNKNOWN,
)
