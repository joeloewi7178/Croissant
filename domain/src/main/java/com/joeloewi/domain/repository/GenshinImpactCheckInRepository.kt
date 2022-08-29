package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse

interface GenshinImpactCheckInRepository {
    suspend fun attendCheckInGenshinImpact(cookie: String): Result<BaseResponse>
}