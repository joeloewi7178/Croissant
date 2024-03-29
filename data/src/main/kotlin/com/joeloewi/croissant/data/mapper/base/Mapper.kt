package com.joeloewi.croissant.data.mapper.base

interface Mapper<DomainEntity, DataEntity> {
    fun toData(domainEntity: DomainEntity): DataEntity
    fun toDomain(dataEntity: DataEntity): DomainEntity
}