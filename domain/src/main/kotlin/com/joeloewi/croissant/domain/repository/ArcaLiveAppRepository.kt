package com.joeloewi.croissant.domain.repository

import com.joeloewi.croissant.domain.common.HoYoLABGame

interface ArcaLiveAppRepository {
    suspend fun getRedeemCode(game: HoYoLABGame): Result<String>
}