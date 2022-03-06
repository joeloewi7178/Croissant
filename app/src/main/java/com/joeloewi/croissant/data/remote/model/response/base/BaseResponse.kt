package com.joeloewi.croissant.data.remote.model.response.base

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any
}