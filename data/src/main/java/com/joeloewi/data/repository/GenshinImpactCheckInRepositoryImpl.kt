package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.GenshinImpactCheckInDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.repository.GenshinImpactCheckInRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class GenshinImpactCheckInRepositoryImpl @Inject constructor(
    private val genshinImpactCheckInDataSource: GenshinImpactCheckInDataSource
) : GenshinImpactCheckInRepository {

    override suspend fun attendCheckInGenshinImpact(cookie: String): Result<BaseResponse> =
        genshinImpactCheckInDataSource.runCatching {
            attendCheckInGenshinImpact(cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }
}