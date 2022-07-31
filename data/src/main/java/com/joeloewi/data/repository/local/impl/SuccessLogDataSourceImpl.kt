package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.SuccessLogDao
import com.joeloewi.data.mapper.SuccessLogMapper
import com.joeloewi.data.repository.local.SuccessLogDataSource
import com.joeloewi.domain.entity.SuccessLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SuccessLogDataSourceImpl @Inject constructor(
    private val successLogDao: SuccessLogDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val successLogMapper: SuccessLogMapper,
) : SuccessLogDataSource {

    override suspend fun insert(successLog: SuccessLog): Long = withContext(coroutineDispatcher) {
        successLogDao.insert(successLogMapper.toData(successLog))
    }

}