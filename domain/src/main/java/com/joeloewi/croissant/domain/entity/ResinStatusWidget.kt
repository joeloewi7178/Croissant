package com.joeloewi.croissant.domain.entity

import java.util.*

data class ResinStatusWidget(
    val id: Long = 0,
    val appWidgetId: Int = 0,
    val interval: Long = 0,
    val refreshGenshinResinStatusWorkerName: UUID = UUID.randomUUID()
)
