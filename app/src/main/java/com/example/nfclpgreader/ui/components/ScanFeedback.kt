package com.example.nfclpgreader.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.nfclpgreader.R

/**
 * Sound + haptics for the scan. Files live in res/raw: scan_ignite, scan_success, scan_error.
 * Needs <uses-permission android:name="android.permission.VIBRATE"/> in the manifest.
 */
class ScanFeedback(context: Context) {
    var soundEnabled = true
    /** Soft chime right after the ignite sound on success. */
    var chimeAfterIgnite = true
    private val handler = Handler(Looper.getMainLooper())
    private val vibrator: Vibrator? = context.getSystemService(Vibrator::class.java)
    private val loaded = HashSet<Int>()
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    init {
        pool.setOnLoadCompleteListener { _, id, status ->
            if (status == 0) synchronized(loaded) { loaded.add(id) }
        }
    }

    private val ignite = pool.load(context, R.raw.scan_ignite, 1)
    private val success = pool.load(context, R.raw.scan_success, 1)
    private val error = pool.load(context, R.raw.scan_error, 1)

    private fun play(id: Int) {
        if (soundEnabled && synchronized(loaded) { id in loaded }) {
            pool.play(id, 1f, 1f, 1, 0, 1f)
        }
    }

    @SuppressLint("MissingPermission")
    private fun buzz(vararg t: Long) {
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createWaveform(t, -1))
        }
    }

    /** Tag first seen: haptic only (the ignite sound is saved for the tick). */
    fun onTagDetected() {
        buzz(0, 14, 45, 14, 60, 90)
    }

    /** Tick appears: igniter ticks + whoosh, then a chime. */
    fun onSuccess() {
        play(ignite)
        buzz(0, 35)
        if (chimeAfterIgnite) {
            handler.postDelayed({ play(success) }, 450)
        }
    }

    /** Ignition lap done and the card failed. */
    fun onError() {
        play(error)
        buzz(0, 40, 60, 40, 60, 40)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        pool.release()
    }
}
