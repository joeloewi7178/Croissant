package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity
import java.util.UUID

@Immutable
data class ResinStatusWidget(
    val id: Long = 0,
    val appWidgetId: Int = 0,
    val interval: Long = 0,
    val refreshGenshinResinStatusWorkerName: UUID = UUID.randomUUID()
)

fun ResinStatusWidgetEntity.asExternalData(): ResinStatusWidget = with(this) {
    ResinStatusWidget(id, appWidgetId, interval, refreshGenshinResinStatusWorkerName)
}

fun ResinStatusWidget.asData(): ResinStatusWidgetEntity = with(this) {
    ResinStatusWidgetEntity(id, appWidgetId, interval, refreshGenshinResinStatusWorkerName)
}
