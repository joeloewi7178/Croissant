package com.joeloewi.croissant.data.util

import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

suspend fun <T : Any> runAndRetryWithExponentialBackOff(
    initialDelay: Int = 500,
    retryFactor: Float = 2f,
    maxAttempts: Int = 5,
    maxDelayMillis: Long = 3000,
    task: suspend () -> ApiResponse<T>,
): ApiResponse<T> = retryTask(
    attempt = 1,
    initialDelay = initialDelay,
    retryFactor = retryFactor,
    maxAttempts = maxAttempts,
    maxDelayMillis = maxDelayMillis,
    task = task
)

internal tailrec suspend fun <T : Any> retryTask(
    attempt: Int = 1,
    initialDelay: Int,
    retryFactor: Float,
    maxAttempts: Int,
    maxDelayMillis: Long,
    task: suspend () -> ApiResponse<T>
): ApiResponse<T> {
    val isRetrying = attempt > 1

    if (isRetrying) {
        val fullJitterExponentialBackOffDelay = Random.nextLong(
            0,
            min(
                maxDelayMillis,
                (initialDelay * retryFactor.pow(attempt - 1)).toLong()
            )
        )

        delay(fullJitterExponentialBackOffDelay)
    }

    return when (val apiResponse = task()) {
        is ApiResponse.Success -> apiResponse
        is ApiResponse.Failure -> {
            if (attempt < maxAttempts) {
                retryTask(
                    attempt = attempt + 1,
                    initialDelay = initialDelay,
                    retryFactor = retryFactor,
                    maxAttempts = maxAttempts,
                    maxDelayMillis = maxDelayMillis,
                    task = task
                )
            } else {
                apiResponse
            }
        }
    }
}