package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse

interface TearsOfThemisCheckInRepository {
    suspend fun attendCheckInTearsOfThemis(cookie: String): Result<BaseResponse>
}