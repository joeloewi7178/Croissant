package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HonkaiImpact3rdCheckInDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.repository.HonkaiImpact3rdCheckInRepository
import com.joeloewi.domain.wrapper.ContentOrError
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HonkaiImpact3rdCheckInRepositoryImpl @Inject constructor(
    private val honkaiImpact3rdCheckInDataSource: HonkaiImpact3rdCheckInDataSource
) : HonkaiImpact3rdCheckInRepository {

    override suspend fun attendCheckInHonkaiImpact3rd(cookie: String): ContentOrError<BaseResponse> =
        honkaiImpact3rdCheckInDataSource.attendCheckInHonkaiImpact3rd(cookie = cookie).runCatching {
            getOrThrow().also { response ->
                when (HoYoLABRetCode.findByCode(response.retcode)) {
                    HoYoLABRetCode.LoginFailed -> {
                        throw HoYoLABException.LoginFailedException
                    }
                    HoYoLABRetCode.OK -> {

                    }
                    HoYoLABRetCode.Unknown -> {
                        throw HoYoLABException.Unknown(
                            retCode = response.retcode,
                            responseMessage = response.message
                        )
                    }
                    else -> {

                    }
                }
            }
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )
}