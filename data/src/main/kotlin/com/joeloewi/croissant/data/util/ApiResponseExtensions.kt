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
    maxDelayMillis: Long = 5000,
    task: suspend () -> ApiResponse<T>,
): ApiResponse<T> {
    var unsuccessfulAttempts = 0
    var apiResponse: ApiResponse<T>

    while (true) {
        if (unsuccessfulAttempts > 1) {
            val fullJitterExponentialBackOffDelay = Random.nextLong(
                0,
                min(
                    maxDelayMillis,
                    (initialDelay * retryFactor.pow(unsuccessfulAttempts)).toLong()
                )
            )

            delay(fullJitterExponentialBackOffDelay)
        }

        apiResponse = task()
        when (apiResponse) {
            is ApiResponse.Success -> break
            is ApiResponse.Failure -> {
                when (apiResponse) {
                    is ApiResponse.Failure.Error -> {
                        break
                    }

                    is ApiResponse.Failure.Exception -> {
                        if (unsuccessfulAttempts <= maxAttempts) {
                            unsuccessfulAttempts += 1
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