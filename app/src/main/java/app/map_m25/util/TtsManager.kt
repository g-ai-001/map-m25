package app.map_m25.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var textToSpeech: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.CHINESE)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    _isInitialized.value = true
                    setupUtteranceListener()
                }
            }
        }
    }

    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
            }
        })
    }

    fun speak(text: String) {
        if (_isInitialized.value && text.isNotBlank()) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "map_tts")
        }
    }

    fun speakNavigation(distance: Float, direction: String) {
        val directionText = when (direction) {
            "left" -> "左转"
            "right" -> "右转"
            "straight" -> "直行"
            "arrived" -> "到达目的地"
            else -> direction
        }

        val distanceText = when {
            distance < 1 -> "${(distance * 1000).toInt()}米"
            distance >= 1 -> String.format("%.1f公里", distance)
            else -> ""
        }

        val speech = if (direction == "arrived") {
            "到达目的地"
        } else {
            "${distanceText}后，$directionText"
        }
        speak(speech)
    }

    fun speakLocation(name: String, address: String) {
        speak("当前位置：$name，$address")
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        _isInitialized.value = false
    }
}