/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.core.network.model.response

import com.joeloewi.croissant.core.model.BaseResponse
import com.joeloewi.croissant.core.model.GenshinDailyNoteDataEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenshinDailyNoteResponse(
    @Json(name = "retcode")
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: GenshinDailyNoteDataEntity?
) : BaseResponse
