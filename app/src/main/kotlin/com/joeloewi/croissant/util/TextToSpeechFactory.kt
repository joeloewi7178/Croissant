package com.joeloewi.croissant.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.joeloewi.croissant.state.foldAsLce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import java.util.Locale

class TextToSpeechFactory(
    private val context: Context
) {
    val flow = callbackFlow {
        var textToSpeech: TextToSpeech? = null

        textToSpeech = TextToSpeech(context) { status ->
            trySend(textToSpeech to status)
        }

        awaitClose {
            with(textToSpeech) {
                stop()
                shutdown()
            }
        }
    }.mapLatest { (textToSpeech, state) ->
        runCatching {
            if (state != TextToSpeech.SUCCESS) {
                throw IllegalStateException()
            }
            textToSpeech!!.apply {
                val currentLocale = Locale.getDefault()
                val targetLocale = if (currentLocale in availableLanguages) {
                    currentLocale
                } else {
                    Locale.ENGLISH
                }
                language = targetLocale
            }
        }.foldAsLce()
    }.flowOn(Dispatchers.Default)
}