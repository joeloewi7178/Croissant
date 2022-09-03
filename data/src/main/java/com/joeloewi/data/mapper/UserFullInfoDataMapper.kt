package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.UserFullInfoDataEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.UserFullInfoData

class UserFullInfoDataMapper(
    private val userInfoMapper: UserInfoMapper
) : ReadOnlyMapper<UserFullInfoData, UserFullInfoDataEntity> {

    override fun toDomain(dataEntity: UserFullInfoDataEntity): UserFullInfoData = with(dataEntity) {
        UserFullInfoData(userInfoMapper.toDomain(userInfo))
    }
}