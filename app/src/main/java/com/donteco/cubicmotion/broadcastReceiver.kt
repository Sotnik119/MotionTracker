package com.donteco.cubicmotion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val startServiceIntent = Intent(context, TrackingService::class.java)
        context?.startService(startServiceIntent)
    }
}

const val BROADCAST_ACTION = "com.donteco.motionTracker.STATUS_BROADCAST"
