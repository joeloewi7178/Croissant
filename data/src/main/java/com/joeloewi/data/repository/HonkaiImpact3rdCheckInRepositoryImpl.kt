package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HonkaiImpact3rdCheckInDataSource
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