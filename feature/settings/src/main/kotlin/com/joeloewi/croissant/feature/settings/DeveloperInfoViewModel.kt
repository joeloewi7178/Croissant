package com.joeloewi.croissant.feature.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.core.ui.LCE
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.util.TextToSpeechFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DeveloperInfoViewModel @Inject constructor(
    textToSpeechFactory: TextToSpeechFactory,
    private val packageManager: PackageManager
) : ViewModel(), ContainerHost<DeveloperInfoViewModel.State, DeveloperInfoViewModel.SideEffect> {
    private val _textToSpeech = textToSpeechFactory.flow.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LCE.Loading
    )

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            reduce {
                state.copy(
                    canLaunchGithubIntent = state.githubIntent.resolveActivity(packageManager) != null,
                    canLaunchEmailIntent = state.emailIntent.resolveActivity(packageManager) != null
                )
            }
        }

        intent { _textToSpeech.collect { reduce { state.copy(textToSpeech = it) } } }
    }

    fun onLaunchIntent(intent: Intent) = intent { postSideEffect(SideEffect.LaunchIntent(intent)) }
    fun onSpeakText(text: String, utteranceId: String) = intent {
        val currentTextToSpeech = state.textToSpeech

        if (currentTextToSpeech is LCE.Content) {
            postSideEffect(
                SideEffect.SpeakText(
                    textToSpeech = currentTextToSpeech.content,
                    text = text,
                    utteranceId = utteranceId
                )
            )
        }
    }

    data class State(
        val developerGithub: String = "https://github.com/joeloewi7178",
        val githubIntent: Intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(developerGithub)
        ),
        val canLaunchGithubIntent: Boolean = true,
        val developerEmail: String = "joeloewi7178@gmail.com",
        val emailIntent: Intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, developerEmail)
        },
        val canLaunchEmailIntent: Boolean = true,
        val textToSpeech: LCE<TextToSpeech> = LCE.Loading
    )

    sealed class SideEffect {
        data class LaunchIntent(
            val intent: Intent
        ) : SideEffect()

        data class SpeakText(
            val textToSpeech: TextToSpeech,
            val text: String,
            val utteranceId: String
        ) : SideEffect()
    }
}