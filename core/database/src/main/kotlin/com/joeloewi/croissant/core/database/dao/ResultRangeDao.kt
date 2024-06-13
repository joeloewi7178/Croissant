package com.joeloewi.croissant.core.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultRangeDao {

    @RawQuery(
        observedEntities = [
            WorkerExecutionLogEntity::class
        ]
    )
    fun getStartToEnd(
        query: SupportSQLiteQuery
    ): Flow<com.joeloewi.croissant.core.data.model.ResultRange>
}