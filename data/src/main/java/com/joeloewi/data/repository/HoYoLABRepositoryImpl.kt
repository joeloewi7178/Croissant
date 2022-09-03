package com.joeloewi.data.repository

import com.joeloewi.data.mapper.GameRecordCardDataMapper
import com.joeloewi.data.mapper.GenshinDailyNoteDataMapper
import com.joeloewi.data.mapper.UserFullInfoMapper
import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.domain.common.HoYoLABRetCode
import com.joeloewi.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfo
import com.joeloewi.domain.repository.HoYoLABRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource,
    private val userFullInfoMapper: UserFullInfoMapper,
    private val gameRecordCardDataMapper: GameRecordCardDataMapper,
    private val genshinDailyNoteDataMapper: GenshinDailyNoteDataMapper
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): Result<UserFullInfo> =
        hoYoLABDataSource.runCatching {
            getUserFullInfo(cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }.mapCatching {
            userFullInfoMapper.toDomain(it)
        }

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<GameRecordCardData?> =
        hoYoLABDataSource.runCatching {
            getGameRecordCard(cookie, uid).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }.data!!
        }.mapCatching {
            gameRecordCardDataMapper.toDomain(it)
        }

    override suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String
    ): Result<GenshinDailyNoteData?> = hoYoLABDataSource.runCatching {
        getGenshinDailyNote(
            cookie = cookie, roleId = roleId, server = server
        ).getOrThrow().also { response ->
            if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                throw HoYoLABUnsuccessfulResponseException(
                    responseMessage = response.message,
                    retCode = response.retCode
                )
            }
        }.data!!
    }.mapCatching {
        genshinDailyNoteDataMapper.toDomain(it)
    }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<BaseResponse> =
        hoYoLABDataSource.runCatching {
            changeDataSwitch(cookie, switchId, isPublic, gameId).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }
}