package com.joeloewi.croissant.data.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArticleResponse(
    /*val isEditable: Boolean = false,
    val isDeletable: Boolean = false,
    val isReportable: Boolean = false,
    val id: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val title: String,
    val category: String? = null,
    val contentType: String = "",*/
    val content: String = "",
    /*val commentCount: Long = Long.MIN_VALUE,
    val lastComment: String = "",
    val viewCount: Long = Long.MIN_VALUE,
    val ratingUp: Long = Long.MIN_VALUE,
    val ratingDown: Long = Long.MIN_VALUE,
    val ratingUpIp: Long = Long.MIN_VALUE,
    val ratingDownIp: Long = Long.MIN_VALUE,
    val createdAt: String = "",
    val updatedAt: String = "",
    val publicId: String? = null,
    val token: String = "",
    val isUser: Boolean = false,
    val gravatar: String = "",
    val preventDelete: Boolean = false,
    val channelPermission: Any,
    val captcha: Boolean = false,
    val isSensitive: Boolean = false,
    val categoryDisplayName: String? = null,
    val vote: List<Any> = listOf()*/
)
