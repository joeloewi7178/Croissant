package com.joeloewi.croissant.data.mapper.base

interface ReadOnlyMapper<DomainEntity, DataEntity> {
    fun toDomain(dataEntity: DataEntity): DomainEntity
}