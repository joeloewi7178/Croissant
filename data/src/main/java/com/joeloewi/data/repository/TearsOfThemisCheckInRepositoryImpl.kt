package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
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
            getOrThrow()
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )
}