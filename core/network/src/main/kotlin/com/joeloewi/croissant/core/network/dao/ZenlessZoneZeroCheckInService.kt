package com.joeloewi.croissant.core.network.dao

import com.joeloewi.croissant.core.network.model.response.HoYoLABResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Locale

/*
 * Copyright (C) 2024 joeloewi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
interface ZenlessZoneZeroCheckInService {

    @POST("event/luna/zzz/os/sign")
    suspend fun attend(
        @Header("Cookie") cookie: String,
        @Body params: Map<String, String> = mapOf(
            "act_id" to "e202406031448091",
            "lang" to Locale.getDefault().toLanguageTag().lowercase()
        )
    ): ApiResponse<HoYoLABResponse.AttendanceResponse>
}