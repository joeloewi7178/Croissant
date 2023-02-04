package com.joeloewi.croissant.data.common

import com.squareup.moshi.Moshi
import java.security.MessageDigest
import kotlin.random.Random

fun generateDS(
    headerInformation: HeaderInformation,
    body: Any? = null,
    query: List<Pair<String, Any>>? = listOf()
): String {
    val timePairKey = "t"
    val randomPairKey = "r"
    val saltPairKey = "salt"

    val timePair = timePairKey to (System.currentTimeMillis() / 1000).toString()

    return when (headerInformation) {
        HeaderInformation.OS -> {
            val randomStringPair = randomPairKey to buildString {
                repeat(6) {
                    append(('a'..'z').random())
                }
            }

            val pairs = listOf(
                saltPairKey to headerInformation.saltKey,
                timePair,
                randomStringPair
            )

            val joinedPairsString = pairs.joinToString(
                separator = "&"
            ) {
                with(it) {
                    "${first}=${second}"
                }
            }

            "${timePair.second},${randomStringPair.second},${md5(joinedPairsString).toHex()}"
        }
        HeaderInformation.CN -> {
            val bodyPairKey = "b"
            val queryPairKey = "q"

            val randomStringPair = randomPairKey to "${Random.nextInt(100001, 200000)}"
            val bodyPair =
                bodyPairKey to (Moshi.Builder().build().adapter(Any::class.java).toJson(body) ?: "")
            val queryPair = queryPairKey to (query?.map { with(it) { "${first}=${second}" } } ?: "")

            val pairs = listOf(
                saltPairKey to headerInformation.saltKey,
                timePair,
                randomStringPair,
                bodyPair,
                queryPair
            )

            val joinedPairsString = pairs.joinToString(
                separator = "&"
            ) {
                with(it) {
                    "${first}=${second}"
                }
            }

            "${timePair.second},${randomStringPair.second},${md5(joinedPairsString).toHex()}"
        }
    }
}

private fun md5(str: String): ByteArray =
    MessageDigest.getInstance("MD5").digest(str.toByteArray())

private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }