package com.joeloewi.data.mapper

import com.joeloewi.data.entity.remote.UserInfoEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.UserInfo

class UserInfoMapper : ReadOnlyMapper<UserInfo, UserInfoEntity> {

    override fun toDomain(dataEntity: UserInfoEntity): UserInfo = with(dataEntity) {
        UserInfo(uid, nickname)
    }
}