package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.FailureLogDao
import com.joeloewi.data.mapper.FailureLogMapper
import com.joeloewi.data.repository.local.FailureLogDataSource
import com.joeloewi.domain.entity.FailureLog
import javax.inject.Inject

class FailureLogDataSourceImpl @Inject constructor(
    private val failureLogDao: FailureLogDao,
    private val failureLogMapper: FailureLogMapper
) : FailureLogDataSource {

    override suspend fun insert(failureLog: FailureLog): Long =
        failureLogDao.insert(failureLogMapper.toData(failureLog))
}