package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.database.model.DataWorkerExecutionLogState

enum class WorkerExecutionLogState {
    SUCCESS, FAILURE;
}

fun DataWorkerExecutionLogState.asExternalData(): WorkerExecutionLogState = when (this) {
    DataWorkerExecutionLogState.SUCCESS -> WorkerExecutionLogState.SUCCESS
    DataWorkerExecutionLogState.FAILURE -> WorkerExecutionLogState.FAILURE
}

fun WorkerExecutionLogState.asData(): DataWorkerExecutionLogState = when (this) {
    WorkerExecutionLogState.SUCCESS -> DataWorkerExecutionLogState.SUCCESS
    WorkerExecutionLogState.FAILURE -> DataWorkerExecutionLogState.FAILURE
}