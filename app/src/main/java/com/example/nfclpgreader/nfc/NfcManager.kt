package com.example.nfclpgreader.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build

/**
 * NFC Status representing device hardware capabilities and user settings.
 */
sealed class NfcStatus {
    object Ready : NfcStatus()
    object Disabled : NfcStatus()
    object NotSupported : NfcStatus()
}

/**
 * Manager handling NFC hardware lifecycle, Reader Mode, and Foreground Dispatch.
 */
class NfcManager(private val context: Context) {

    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(context.applicationContext)
    }

    /**
     * Checks the current NFC status of the device.
     */
    fun getNfcStatus(): NfcStatus {
        val adapter = nfcAdapter ?: return NfcStatus.NotSupported
        return if (adapter.isEnabled) NfcStatus.Ready else NfcStatus.Disabled
    }

    /**
     * Enables Reader Mode for modern, high-performance foreground NFC scanning without system sounds.
     */
    fun enableReaderMode(activity: Activity, onTagDiscovered: (Tag) -> Unit) {
        val adapter = nfcAdapter ?: return
        if (!adapter.isEnabled) return

        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V

        adapter.enableReaderMode(activity, { tag ->
            onTagDiscovered(tag)
        }, flags, null)
    }

    /**
     * Disables Reader Mode when activity pauses.
     */
    fun disableReaderMode(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }

    /**
     * Enables traditional Foreground Dispatch as fallback.
     */
    @Suppress("DEPRECATION")
    fun enableForegroundDispatch(activity: Activity) {
        val adapter = nfcAdapter ?: return
        if (!adapter.isEnabled) return

        val intent = Intent(activity, activity.javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, pendingIntentFlags)
        val ndefFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (_: Exception) {}
        }
        val techFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val tagFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)

        adapter.enableForegroundDispatch(activity, pendingIntent, arrayOf(ndefFilter, techFilter, tagFilter), null)
    }

    /**
     * Disables Foreground Dispatch.
     */
    fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }
}
