package com.joeloewi.data.mapper

import com.joeloewi.data.api.model.response.UserFullInfoResponse
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.UserFullInfo

class UserFullInfoMapper(
    private val userFullInfoDataMapper: UserFullInfoDataMapper
) : ReadOnlyMapper<UserFullInfo, UserFullInfoResponse> {

    override fun toDomain(dataEntity: UserFullInfoResponse): UserFullInfo = with(dataEntity) {
        UserFullInfo(retCode, message, data?.let { userFullInfoDataMapper.toDomain(it) })
    }
}