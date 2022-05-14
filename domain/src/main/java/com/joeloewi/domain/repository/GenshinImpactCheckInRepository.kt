package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.wrapper.ContentOrError

interface GenshinImpactCheckInRepository {
    suspend fun attendCheckInGenshinImpact(cookie: String): ContentOrError<BaseResponse>
}