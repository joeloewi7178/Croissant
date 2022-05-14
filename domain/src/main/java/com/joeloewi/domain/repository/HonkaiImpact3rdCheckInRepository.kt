package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.wrapper.ContentOrError

interface HonkaiImpact3rdCheckInRepository {
    suspend fun attendCheckInHonkaiImpact3rd(cookie: String): ContentOrError<BaseResponse>
}