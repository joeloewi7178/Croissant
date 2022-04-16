package com.joeloewi.croissant.data.local.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.local.model.FailureLog
import com.joeloewi.croissant.data.local.model.SuccessLog

data class WorkerExecutionLogWithState(
    @Embedded val workerExecutionLog: WorkerExecutionLog = WorkerExecutionLog(),
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    )
    val successLog: SuccessLog? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    )
    val failureLog: FailureLog? = null,
)