package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.HoYoLABGame

interface ArcaLiveAppRepository {
    suspend fun getRedeemCode(game: HoYoLABGame): Result<String>
}