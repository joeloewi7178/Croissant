package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HonkaiImpact3rdCheckInDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.repository.HonkaiImpact3rdCheckInRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HonkaiImpact3rdCheckInRepositoryImpl @Inject constructor(
    private val honkaiImpact3rdCheckInDataSource: HonkaiImpact3rdCheckInDataSource
) : HonkaiImpact3rdCheckInRepository {

    override suspend fun attendCheckInHonkaiImpact3rd(cookie: String): Result<BaseResponse> =
        honkaiImpact3rdCheckInDataSource.runCatching {
            attendCheckInHonkaiImpact3rd(cookie = cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }
}