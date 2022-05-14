package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.domain.entity.*
import com.joeloewi.domain.repository.HoYoLABRepository
import com.joeloewi.domain.wrapper.ContentOrError
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource
) : HoYoLABRepository {
    override suspend fun getUserFullInfo(cookie: String): ContentOrError<UserFullInfoResponse> =
        hoYoLABDataSource.getUserFullInfo(cookie).runCatching {
            getOrThrow()
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )

    override suspend fun getGameRecordCard(cookie: String, uid: Long): ContentOrError<GameRecordCardData?> =
        hoYoLABDataSource.getGameRecordCard(cookie, uid).runCatching {
            getOrThrow()
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
    ): ContentOrError<GenshinDailyNoteData?> = hoYoLABDataSource.getGenshinDailyNote(
        cookie = cookie, roleId = roleId, server = server
    ).runCatching {
        getOrThrow()
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
        hoYoLABDataSource.changeDataSwitch(cookie, switchId, isPublic, gameId).runCatching {
            getOrThrow()
        }.fold(
            onSuccess = {
                ContentOrError.Content(it)
            },
            onFailure = {
                ContentOrError.Error(it)
            }
        )
}