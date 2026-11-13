package com.example.nfclpgreader.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin

class SoundHelper(private val context: Context) {

    /**
     * Synthesizes a clean dual-frequency chord (C5 + G5) in real-time,
     * matching the chime without needing external audio asset files.
     */
    fun playSuccessChime() {
        thread(start = true) {
            try {
                val sampleRate = 44100
                val durationMs = 380
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val freq1 = 523.25 // C5
                val freq2 = 783.99 // G5

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val decay = 1.0 - (i.toDouble() / numSamples) // smooth linear decay
                    val sample = ((sin(2.0 * Math.PI * freq1 * time) + sin(2.0 * Math.PI * freq2 * time)) / 2.0) * decay
                    buffer[i] = (sample * Short.MAX_VALUE * 0.75).toInt().toShort()
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
