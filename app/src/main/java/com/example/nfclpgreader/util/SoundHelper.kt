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
     * Synthesizes a luxury 3-stage ascending crystal victory chime (E5 -> G#5 -> B5 + E6 + G#6 chord)
     * with exponential bell decay, producing a rich 3D gold coin victory audio feedback.
     */
    fun playSuccessChime() {
        thread(start = true) {
            try {
                val sampleRate = 44100
                val durationMs = 550
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val stage1Samples = (sampleRate * 0.11).toInt() // 0 to 110ms: E5
                val stage2Samples = (sampleRate * 0.22).toInt() // 110 to 220ms: G#5

                val fE5 = 659.25
                val fGSharp5 = 830.61
                val fB5 = 987.77
                val fE6 = 1318.51
                val fGSharp6 = 1661.22

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val decay = exp(-4.5 * i / numSamples) // Natural bell exponential decay

                    val rawSample = when {
                        i < stage1Samples -> {
                            sin(2.0 * Math.PI * fE5 * time) * 0.7
                        }
                        i < stage2Samples -> {
                            sin(2.0 * Math.PI * fGSharp5 * time) * 0.85
                        }
                        else -> {
                            (sin(2.0 * Math.PI * fB5 * time) +
                             sin(2.0 * Math.PI * fE6 * time) +
                             sin(2.0 * Math.PI * fGSharp6 * time)) / 3.0
                        }
                    }

                    buffer[i] = (rawSample * decay * Short.MAX_VALUE * 0.85).toInt().toShort()
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
