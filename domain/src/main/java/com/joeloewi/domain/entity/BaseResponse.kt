package com.joeloewi.domain.entity

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any?
}