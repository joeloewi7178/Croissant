package com.joeloewi.croissant.core.data.model

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any?
}