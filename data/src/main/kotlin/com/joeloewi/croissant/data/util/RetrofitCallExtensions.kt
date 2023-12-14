package com.joeloewi.croissant.data.util

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Invocation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> Call<T>.executeAndAwait() = suspendCancellableCoroutine<T> { cont ->
    val response = execute()
    cont.invokeOnCancellation { cancel() }

    if (response.isSuccessful) {
        val body = response.body()
        if (body == null) {
            val invocation = request().tag(Invocation::class.java)!!
            val method = invocation.method()
            val e =
                KotlinNullPointerException("Response from ${method.declaringClass.name}.${method.name} was null but response body type was declared as non-null")
            cont.resumeWithException(e)
        } else {
            cont.resume(body)
        }
    } else {
        cont.resumeWithException(HttpException(response))
    }
}