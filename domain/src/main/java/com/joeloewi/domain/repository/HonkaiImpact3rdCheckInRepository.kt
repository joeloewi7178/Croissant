package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse

interface HonkaiImpact3rdCheckInRepository {
    suspend fun attendCheckInHonkaiImpact3rd(cookie: String): Result<BaseResponse>
}