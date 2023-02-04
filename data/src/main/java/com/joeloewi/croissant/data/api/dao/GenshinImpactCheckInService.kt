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

package com.joeloewi.croissant.data.api.dao

import com.joeloewi.croissant.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface GenshinImpactCheckInService {
    @POST("event/sol/sign")
    suspend fun attendCheckInGenshinImpact(
        @Query("act_id") actId: String = "e202102251931481",
        @Query("lang") language: String = Locale.getDefault().toLanguageTag().lowercase(),
        @Header("Cookie") cookie: String
    ): ApiResponse<AttendanceResponse>
}