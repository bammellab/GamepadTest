@file:Suppress("MoveVariableDeclarationIntoWhen")

package com.bammellab.gamepadtest.gamepad

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "Bluetooth"

/**
 * Broadcast receiver for monitorying Bluetooth connectivity changes
 *
 * @link: https://developer.android.com/guide/components/broadcasts
 */
class LocalBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action

        /*StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }*/

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> Log.i(TAG, "off")
                BluetoothAdapter.STATE_TURNING_OFF -> Log.i(TAG, "turning off")
                BluetoothAdapter.STATE_ON -> Log.i(TAG, "on")
                BluetoothAdapter.STATE_TURNING_ON -> Log.i(TAG, "turning on")
            }
        }
    }


    fun setCallback(callbackIn: Callback) {
        callback = callbackIn
    }

    interface Callback {
        fun updateBluetoothStatus(state: Int)
    }

    private var callback: Callback? = null
}