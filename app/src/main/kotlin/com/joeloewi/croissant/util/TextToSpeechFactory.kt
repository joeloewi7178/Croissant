package com.joeloewi.croissant.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.joeloewi.croissant.state.foldAsLce
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class TextToSpeechFactory(
    private val context: Context
) {
    val flow = callbackFlow {
        var textToSpeech: TextToSpeech? = null

        textToSpeech = TextToSpeech(context) { status ->
            trySend(runCatching {
                if (status != TextToSpeech.SUCCESS) {
                    throw IllegalStateException()
                }
                textToSpeech!!.apply {
                    val currentLocale = Locale.getDefault()
                    val targetLocale = if (availableLanguages.contains(currentLocale)) {
                        currentLocale
                    } else {
                        Locale.ENGLISH
                    }
                    language = targetLocale
                }
            }.foldAsLce())
        }

        awaitClose {
            with(textToSpeech) {
                stop()
                shutdown()
            }
        }
    }
}