package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.SuccessLogDao
import com.joeloewi.data.mapper.SuccessLogMapper
import com.joeloewi.data.repository.local.SuccessLogDataSource
import com.joeloewi.domain.entity.SuccessLog
import javax.inject.Inject

class SuccessLogDataSourceImpl @Inject constructor(
    private val successLogDao: SuccessLogDao,
    private val successLogMapper: SuccessLogMapper,
) : SuccessLogDataSource {

    override suspend fun insert(successLog: SuccessLog): Long =
        successLogDao.insert(successLogMapper.toData(successLog))
}