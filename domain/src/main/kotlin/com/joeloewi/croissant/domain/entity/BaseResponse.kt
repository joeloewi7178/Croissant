package com.joeloewi.croissant.domain.entity

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any?
}