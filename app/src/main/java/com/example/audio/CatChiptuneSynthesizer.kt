package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class MelodyTrack(
    val name: String,
    val icon: String,
    val description: String,
    val tempoBpm: Int,
    val notes: List<Pair<Float, Float>> // frequency in Hz, duration in beats
)

class CatChiptuneSynthesizer {

    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMelodyIndex = MutableStateFlow(0)
    val currentMelodyIndex: StateFlow<Int> = _currentMelodyIndex.asStateFlow()

    private val _currentNoteBeat = MutableStateFlow(0)
    val currentNoteBeat: StateFlow<Int> = _currentNoteBeat.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    companion object {
        // Musical note frequencies (Hz)
        const val C4 = 261.63f
        const val D4 = 293.66f
        const val E4 = 329.63f
        const val F4 = 349.23f
        const val G4 = 392.00f
        const val A4 = 440.00f
        const val B4 = 493.88f

        const val C5 = 523.25f
        const val D5 = 587.33f
        const val E5 = 659.25f
        const val F5 = 698.46f
        const val G5 = 783.99f
        const val A5 = 880.00f
        const val B5 = 987.77f
        const val C6 = 1046.50f
        const val REST = 0.0f
    }

    val availableMelodies = listOf(
        MelodyTrack(
            name = "Ronrom de Ninar 🌙",
            icon = "🐱💤",
            description = "Melodia suave em caixa de música para acalmar o gatinho",
            tempoBpm = 110,
            notes = listOf(
                Pair(C5, 1f), Pair(E5, 1f), Pair(G5, 1f), Pair(C6, 2f),
                Pair(B5, 1f), Pair(G5, 1f), Pair(A5, 2f), Pair(F5, 1f),
                Pair(D5, 1f), Pair(E5, 1f), Pair(G5, 1f), Pair(C5, 2f),
                Pair(REST, 0.5f),
                Pair(E5, 1f), Pair(G5, 1f), Pair(A5, 1.5f), Pair(G5, 1f),
                Pair(E5, 1f), Pair(D5, 1f), Pair(C5, 2f)
            )
        ),
        MelodyTrack(
            name = "Patinhas Saltitantes 🐾",
            icon = "🧶✨",
            description = "Ritmo alegre de videogame fofo para celebrar as missões",
            tempoBpm = 135,
            notes = listOf(
                Pair(G4, 0.5f), Pair(C5, 0.5f), Pair(E5, 0.5f), Pair(G5, 1f),
                Pair(E5, 0.5f), Pair(G5, 1.5f),
                Pair(A5, 0.5f), Pair(G5, 0.5f), Pair(E5, 0.5f), Pair(C5, 1f),
                Pair(D5, 1f), Pair(REST, 0.5f),
                Pair(G4, 0.5f), Pair(C5, 0.5f), Pair(E5, 0.5f), Pair(G5, 1f),
                Pair(A5, 0.5f), Pair(C6, 1.5f),
                Pair(B5, 0.5f), Pair(A5, 0.5f), Pair(G5, 0.5f), Pair(E5, 0.5f),
                Pair(C5, 2f)
            )
        ),
        MelodyTrack(
            name = "Brisa dos Bigodes 🌸",
            icon = "🍃💖",
            description = "Uma valsinha doce e carinhosa de lembranças",
            tempoBpm = 120,
            notes = listOf(
                Pair(A4, 1f), Pair(C5, 1f), Pair(E5, 1f), Pair(A5, 2f),
                Pair(G5, 1f), Pair(E5, 1f), Pair(F5, 1.5f), Pair(D5, 1f),
                Pair(E5, 1f), Pair(C5, 1f), Pair(D5, 1.5f), Pair(B4, 1f),
                Pair(C5, 2.5f), Pair(REST, 0.5f)
            )
        ),
        MelodyTrack(
            name = "Valsinha das Sonecas 🎶",
            icon = "💤☁️",
            description = "Harmonias serenas para contemplar os momentos do mês",
            tempoBpm = 100,
            notes = listOf(
                Pair(E4, 1.5f), Pair(G4, 1.5f), Pair(B4, 1.5f), Pair(E5, 2f),
                Pair(D5, 1f), Pair(B4, 1f), Pair(G4, 1.5f), Pair(A4, 1.5f),
                Pair(C5, 1.5f), Pair(E5, 1.5f), Pair(D5, 1.5f), Pair(B4, 1.5f),
                Pair(G4, 2f), Pair(REST, 1f)
            )
        ),
        MelodyTrack(
            name = "Aventura do Sachê ✨",
            icon = "🏆🐟",
            description = "Marcha alegre e triunfante das missões cumpridas",
            tempoBpm = 130,
            notes = listOf(
                Pair(C5, 0.5f), Pair(C5, 0.5f), Pair(E5, 0.5f), Pair(G5, 1f),
                Pair(C6, 1.5f), Pair(B5, 0.5f), Pair(A5, 0.5f), Pair(G5, 1f),
                Pair(F5, 0.5f), Pair(E5, 0.5f), Pair(D5, 1f), Pair(G5, 1f),
                Pair(E5, 0.5f), Pair(D5, 0.5f), Pair(C5, 2f), Pair(REST, 0.5f)
            )
        )
    )

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize.coerceAtLeast(sampleRate / 2)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(melodyIndex: Int = _currentMelodyIndex.value) {
        val safeIndex = melodyIndex.coerceIn(0, availableMelodies.size - 1)
        _currentMelodyIndex.value = safeIndex
        stop()

        if (audioTrack == null || audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
            initAudioTrack()
        }

        try {
            audioTrack?.play()
        } catch (e: Exception) {
            initAudioTrack()
            audioTrack?.play()
        }

        _isPlaying.value = true
        val melody = availableMelodies[safeIndex]
        val beatDurationMs = (60_000f / melody.tempoBpm).toLong()

        playbackJob = scope.launch {
            while (isActive && _isPlaying.value) {
                for ((idx, note) in melody.notes.withIndex()) {
                    if (!isActive || !_isPlaying.value) break
                    _currentNoteBeat.value = idx

                    val (freq, beats) = note
                    val durationMs = (beats * beatDurationMs).toLong()
                    val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()

                    if (freq > 0f && !_isMuted.value) {
                        val buffer = ShortArray(totalSamples)
                        val angularFreq = 2.0 * PI * freq / sampleRate
                        val harmonicFreq = 2.0 * PI * (freq * 2.0) / sampleRate

                        for (i in 0 until totalSamples) {
                            val t = i.toDouble() / sampleRate
                            // Soft bell/music-box envelope: sharp attack, smooth exponential decay
                            val envelope = exp(-3.2 * t)
                            val mainTone = sin(angularFreq * i)
                            val overtone = 0.3 * sin(harmonicFreq * i)
                            val sample = (mainTone + overtone) * envelope * 0.45
                            buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }

                        audioTrack?.write(buffer, 0, buffer.size)
                    } else {
                        // Rest or muted
                        delay(durationMs)
                    }
                }
                delay(600) // Brief silence between loops
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play(_currentMelodyIndex.value)
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun selectMelody(index: Int) {
        val wasPlaying = _isPlaying.value
        _currentMelodyIndex.value = index
        if (wasPlaying) {
            play(index)
        }
    }

    fun release() {
        stop()
        try {
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
}
