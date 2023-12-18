package com.joeloewi.croissant.data.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.joeloewi.croissant.data.entity.local.WorkerExecutionLogEntity
import com.joeloewi.croissant.domain.entity.ResultCount
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
    ): Flow<List<ResultCount>>
}