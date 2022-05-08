package com.joeloewi.domain.entity.relational

import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.entity.WorkerExecutionLog

data class WorkerExecutionLogWithState(
    val workerExecutionLog: WorkerExecutionLog = WorkerExecutionLog(),
    val successLog: SuccessLog? = null,
    val failureLog: FailureLog? = null,
)
