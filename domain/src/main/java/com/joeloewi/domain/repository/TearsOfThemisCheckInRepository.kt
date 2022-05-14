package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.wrapper.ContentOrError

interface TearsOfThemisCheckInRepository {
    suspend fun attendCheckInTearsOfThemis(cookie: String): ContentOrError<BaseResponse>
}