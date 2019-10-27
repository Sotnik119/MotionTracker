package com.donteco.cubicmotion

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*
import kotlin.math.absoluteValue

class TrackingProcessor(
    val dataHolder: DataHolder,
    val broadcaster: LocalBroadcastManager
) {

    private var mTimer = Timer()
    val currentPosition: Position = Position(null, null)
    private lateinit var savedPosition: Position
    private var sens: Float = 1F

    private var state: Boolean = true
        set(value) {
//            if (field != value) {
            MessageSender.sendMessage(
                dataHolder.serverIp,
                dataHolder.serverPort,
                "{" +
                        "\"appName\":\"PhonesPosition\"," +
                        "\"id\":${dataHolder.deviceId}," +
                        " \"inPlace\":$value" +
                        "}"
            )
            broadcaster.sendBroadcast(
                Intent(BROADCAST_ACTION).apply {
                    putExtra("Status", value)
                }
            )
//            }
            field = value
        }


    val accelerateListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, acc: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            currentPosition.AccPosition =
                event.values.asList().subList(0, 3).toTypedArray()
        }
    }


    private fun startTracking() {
        sens = 1F - (dataHolder.sensivity.toFloat() / 100)
        savedPosition = dataHolder.lastSavedPosition

        Log.d("TrackingProcessor", "Start tracking: sens:$sens, timeout:${dataHolder.timeOut}!")
        val task = object : TimerTask() {
            override fun run() {
                state = if (isPositionChanged(savedPosition, currentPosition)) {
                    Log.d("TrackingProcessor", "PositionChanged!")
                    false
                } else {
                    Log.d("TrackingProcessor", "Position - ok!")
                    true
                }
            }
        }

        mTimer.scheduleAtFixedRate(task, 2000, dataHolder.timeOut * 1000)
    }

    fun stopTracking() {
        mTimer.cancel()
        mTimer.purge()
        mTimer = Timer()
    }

    fun restart() {
        stopTracking()
        startTracking()
    }

    fun isPositionChanged(pos1: Position, pos2: Position): Boolean {
        if (pos1.AccPosition != null && pos2.AccPosition != null)
            for (i in 0..2) {
                if ((pos1.AccPosition!![i].absoluteValue - pos2.AccPosition!![i].absoluteValue).absoluteValue > sens)
                    return true
            }
        return false
    }


}
