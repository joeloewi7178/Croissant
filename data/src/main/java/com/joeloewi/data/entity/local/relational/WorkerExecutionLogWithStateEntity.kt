package com.joeloewi.data.entity.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.data.entity.local.FailureLogEntity
import com.joeloewi.data.entity.local.SuccessLogEntity
import com.joeloewi.data.entity.local.WorkerExecutionLogEntity

data class WorkerExecutionLogWithStateEntity(
    @Embedded val workerExecutionLogEntity: WorkerExecutionLogEntity = WorkerExecutionLogEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    ) val successLogEntity: SuccessLogEntity? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "executionLogId"
    ) val failureLogEntity: FailureLogEntity? = null,
)