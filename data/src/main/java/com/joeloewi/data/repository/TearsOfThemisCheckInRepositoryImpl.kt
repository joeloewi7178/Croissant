package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABUnsuccessfulResponseException
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
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
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