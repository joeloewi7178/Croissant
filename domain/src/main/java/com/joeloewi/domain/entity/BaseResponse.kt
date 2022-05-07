package com.joeloewi.domain.entity

interface BaseResponse {
    val retcode: Int
    val message: String
    val data: Any?
}