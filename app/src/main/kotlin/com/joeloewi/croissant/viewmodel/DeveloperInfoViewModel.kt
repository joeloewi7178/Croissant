package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.LCE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import nl.marc_apps.tts.TextToSpeechFactory
import javax.inject.Inject

@HiltViewModel
class DeveloperInfoViewModel @Inject constructor(
    private val textToSpeechFactory: TextToSpeechFactory
) : ViewModel() {
    val textToSpeech = callbackFlow {
        val textToSpeech = textToSpeechFactory.runCatching {
            createOrThrow()
        }.fold(
            onSuccess = {
                LCE.Content(it)
            },
            onFailure = {
                LCE.Error(it)
            }
        )

        trySend(textToSpeech)

        awaitClose { textToSpeech.content?.close() }
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LCE.Loading
    )
}