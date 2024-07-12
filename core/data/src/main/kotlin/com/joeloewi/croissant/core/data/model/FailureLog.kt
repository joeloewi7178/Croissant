package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.database.model.FailureLogEntity

@Immutable
data class FailureLog(
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)

fun FailureLogEntity.asExternalData(): FailureLog = with(this) {
    FailureLog(id, executionLogId, gameName.asExternalData(), failureMessage, failureStackTrace)
}

fun FailureLog.asData(): FailureLogEntity = with(this) {
    FailureLogEntity(id, executionLogId, gameName.asData(), failureMessage, failureStackTrace)
}