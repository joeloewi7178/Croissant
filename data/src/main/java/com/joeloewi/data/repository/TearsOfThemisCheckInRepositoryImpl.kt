package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.repository.TearsOfThemisCheckInRepository
import com.joeloewi.domain.wrapper.ContentOrError
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class TearsOfThemisCheckInRepositoryImpl @Inject constructor(
    private val themisCheckInDataSource: TearsOfThemisCheckInDataSource
) : TearsOfThemisCheckInRepository {

    override suspend fun attendCheckInTearsOfThemis(cookie: String): ContentOrError<BaseResponse> =
        themisCheckInDataSource.attendCheckInTearsOfThemis(cookie = cookie).runCatching {
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