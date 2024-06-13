package com.joeloewi.croissant.core.network.model.response.base

interface BaseResponse {
    val retCode: Int
    val message: String
    val data: Any?
}