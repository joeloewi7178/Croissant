package com.joeloewi.croissant.data.remote.model.response

import com.joeloewi.croissant.data.remote.model.common.GameRecordCardData
import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse

data class GameRecordCard(
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: GameRecordCardData
): BaseResponse
