package com.example.railconnect.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    fun playTileSelect() {
        if (!isEnabled) return
        scope.launch {
            val samples = generateChirp(startFreq = 850.0, endFreq = 950.0, durationMs = 35)
            playPcm(samples)
        }
    }

    fun playRotate() {
        if (!isEnabled) return
        scope.launch {
            val samples = generateMechanicalClick(durationMs = 50)
            playPcm(samples)
        }
    }

    fun playPlacement() {
        if (!isEnabled) return
        scope.launch {
            val samples = generateThud(durationMs = 70)
            playPcm(samples)
        }
    }

    fun playStationConnected() {
        if (!isEnabled) return
        scope.launch {
            val note1 = generateTone(523.25, 90) // C5
            val note2 = generateTone(659.25, 140) // E5
            val combined = note1 + note2
            playPcm(combined)
        }
    }

    fun playNetworkComplete() {
        if (!isEnabled) return
        scope.launch {
            val c5 = generateTone(523.25, 80)
            val e5 = generateTone(659.25, 80)
            val g5 = generateTone(783.99, 100)
            val c6 = generateTone(1046.50, 260)
            val fanfare = c5 + e5 + g5 + c6
            playPcm(fanfare)
        }
    }

    fun playFailure() {
        if (!isEnabled) return
        scope.launch {
            val note1 = generateTone(392.0, 110) // G4
            val note2 = generateTone(329.63, 200) // E4
            val combined = note1 + note2
            playPcm(combined)
        }
    }

    private fun generateTone(freq: Double, durationMs: Int): ShortArray {
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(count)
        val angularFreq = 2.0 * Math.PI * freq / sampleRate
        for (i in 0 until count) {
            val envelope = 1.0 - (i.toDouble() / count) // Linear fade out
            val sample = (sin(angularFreq * i) * Short.MAX_VALUE * 0.35 * envelope).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateChirp(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val freq = startFreq + (endFreq - startFreq) * progress
            val envelope = (1.0 - progress) * 0.28
            val sample = (sin(2.0 * Math.PI * freq * (i.toDouble() / sampleRate)) * Short.MAX_VALUE * envelope).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateMechanicalClick(durationMs: Int): ShortArray {
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val envelope = (1.0 - progress) * 0.32
            val s1 = sin(2.0 * Math.PI * 620.0 * (i.toDouble() / sampleRate))
            val s2 = sin(2.0 * Math.PI * 840.0 * (i.toDouble() / sampleRate))
            val sample = ((s1 + s2) * 0.5 * Short.MAX_VALUE * envelope).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateThud(durationMs: Int): ShortArray {
        val count = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val freq = 340.0 * (1.0 - progress * 0.5)
            val envelope = (1.0 - progress) * (1.0 - progress) * 0.45
            val sample = (sin(2.0 * Math.PI * freq * (i.toDouble() / sampleRate)) * Short.MAX_VALUE * envelope).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun playPcm(pcm: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()
            // Release after playing completes
            scope.launch {
                val duration = (pcm.size * 1000L) / sampleRate + 50
                kotlinx.coroutines.delay(duration)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) { }
            }
        } catch (_: Exception) {
            // AudioTrack unavailable or muted
        }
    }
}
