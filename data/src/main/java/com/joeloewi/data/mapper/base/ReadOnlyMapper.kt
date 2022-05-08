package com.joeloewi.data.mapper.base

interface ReadOnlyMapper<DomainEntity, DataEntity> {
    fun toDomain(dataEntity: DataEntity): DomainEntity
}