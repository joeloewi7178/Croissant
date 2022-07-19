package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfoResponse
import com.joeloewi.domain.repository.HoYoLABRepository
import com.joeloewi.domain.wrapper.ContentOrError
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): ContentOrError<UserFullInfoResponse> =
        hoYoLABDataSource.runCatching {
            getUserFullInfo(cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): ContentOrError<GameRecordCardData?> =
        hoYoLABDataSource.runCatching {
            getGameRecordCard(cookie, uid).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }.fold(
            onSuccess = {
                ContentOrError.Content(it.data)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )

    override suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String
    ): ContentOrError<GenshinDailyNoteData?> = hoYoLABDataSource.runCatching {
        getGenshinDailyNote(
            cookie = cookie, roleId = roleId, server = server
        ).getOrThrow().also { response ->
            if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                throw HoYoLABUnsuccessfulResponseException(
                    responseMessage = response.message,
                    retCode = response.retCode
                )
            }
        }
    }.fold(
        onSuccess = {
            ContentOrError.Content(it.data)
        },
        onFailure = {
            ContentOrError.Error(it)
        }
    )

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): ContentOrError<BaseResponse> =
        hoYoLABDataSource.runCatching {
            changeDataSwitch(cookie, switchId, isPublic, gameId).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )
}