package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.database.model.DataLoggableWorker

enum class LoggableWorker {
    ATTEND_CHECK_IN_EVENT, CHECK_SESSION, UNKNOWN;
}

fun DataLoggableWorker.asExternalData(): LoggableWorker = when (this) {
    DataLoggableWorker.ATTEND_CHECK_IN_EVENT -> LoggableWorker.ATTEND_CHECK_IN_EVENT
    DataLoggableWorker.CHECK_SESSION -> LoggableWorker.CHECK_SESSION
    DataLoggableWorker.UNKNOWN -> LoggableWorker.UNKNOWN
}

fun LoggableWorker.asData(): DataLoggableWorker = when (this) {
    LoggableWorker.ATTEND_CHECK_IN_EVENT -> DataLoggableWorker.ATTEND_CHECK_IN_EVENT
    LoggableWorker.CHECK_SESSION -> DataLoggableWorker.CHECK_SESSION
    LoggableWorker.UNKNOWN -> DataLoggableWorker.UNKNOWN
}