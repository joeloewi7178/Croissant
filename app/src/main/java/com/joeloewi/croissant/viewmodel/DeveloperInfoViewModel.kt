package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import nl.marc_apps.tts.TextToSpeech
import okhttp3.internal.closeQuietly
import javax.inject.Inject

@HiltViewModel
class DeveloperInfoViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    val textToSpeech = callbackFlow {
        val textToSpeech = TextToSpeech.runCatching {
            createOrThrow(application)
        }.fold(
            onSuccess = {
                Lce.Content(it)
            },
            onFailure = {
                Lce.Error(it)
            }
        )

        trySend(textToSpeech)

        awaitClose { textToSpeech.content?.closeQuietly() }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Lce.Loading
    )
}