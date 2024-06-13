package com.joeloewi.croissant.core.model

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any?
}