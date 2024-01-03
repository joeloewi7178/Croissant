package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.util.TextToSpeechFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DeveloperInfoViewModel @Inject constructor(
    textToSpeechFactory: TextToSpeechFactory
) : ViewModel() {
    val textToSpeech = textToSpeechFactory.flow.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LCE.Loading
    )
}