package com.joeloewi.croissant.data.util

import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T : Any> runAndRetryWithExponentialBackOff(
    initialDelay: Int = 500,
    retryFactor: Float = 2f,
    maxAttempts: Int = 3,
    task: suspend () -> ApiResponse<T>,
): ApiResponse<T> {
    var attempt = 1
    var apiResponse: ApiResponse<T>

    while (true) {
        apiResponse = task()
        when (val cached = apiResponse) {
            is ApiResponse.Success -> break
            is ApiResponse.Failure -> {
                when (cached) {
                    is ApiResponse.Failure.Error -> {
                        break
                    }

                    is ApiResponse.Failure.Exception -> {
                        if (attempt < maxAttempts) {
                            attempt += 1
                            delay((initialDelay * retryFactor.pow(attempt.toFloat())).toLong().milliseconds)
                        } else {
                            break
                        }
                    }
                }
            }
        }
    }

    return apiResponse
}