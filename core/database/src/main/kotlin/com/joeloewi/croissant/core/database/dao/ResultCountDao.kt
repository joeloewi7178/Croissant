package com.joeloewi.croissant.core.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultCountDao {

    @RawQuery(
        observedEntities = [
            WorkerExecutionLogEntity::class
        ]
    )
    fun getAll(
        query: SupportSQLiteQuery
    ): Flow<List<com.joeloewi.croissant.core.data.model.ResultCount>>
}