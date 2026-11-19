package com.example.nfclpgreader.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.exp
import kotlin.math.sin

class SoundHelper(private val context: Context) {

    /**
     * Synthesizes a crystal-clear 4-tone harmonic chord (C5 + E5 + G5 + C6) with exponential bell decay,
     * producing a luxury, high-end verification chime without needing external asset dependencies.
     */
    fun playSuccessChime() {
        thread(start = true) {
            try {
                val sampleRate = 44100
                val durationMs = 500
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val f1 = 523.25  // C5
                val f2 = 659.25  // E5
                val f3 = 783.99  // G5
                val f4 = 1046.50 // C6 (Crystal High Pitch)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val decay = exp(-5.0 * i / numSamples) // Natural bell decay
                    val sample = ((sin(2.0 * Math.PI * f1 * time) +
                                   sin(2.0 * Math.PI * f2 * time) +
                                   sin(2.0 * Math.PI * f3 * time) +
                                   sin(2.0 * Math.PI * f4 * time)) / 4.0) * decay
                    buffer[i] = (sample * Short.MAX_VALUE * 0.85).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
