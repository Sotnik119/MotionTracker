package com.donteco.cubicmotion

import android.content.Context

class DataHolder(context: Context) {

    private val prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)

    var enableTracking: Boolean
        get() = prefs.getBoolean("tracking", false)
        set(value) {
            prefs.edit().putBoolean("tracking", value).apply()
        }


    var lastSavedPosition: Position
        get() = prefs.getString("lastSavedPosition", null)?.fromJson<Position>() ?: Position(
            null,
            null
        )
        set(value) = prefs.edit().putString("lastSavedPosition", value.toJson()).apply()

    var timeOut: Long
        get() = prefs.getLong("timeOut", 1L)
        set(value) {
            prefs.edit().putLong("timeOut", value).apply()
        }

    var sensivity: Int
        get() = prefs.getInt("sensivity", 50)
        set(value) {
            prefs.edit().putInt("sensivity", value).apply()
        }

    var deviceId: Int
        get() = prefs.getInt("deviceId", 1)
        set(value) {
            prefs.edit().putInt("deviceId", value).apply()
        }

    var serverIp: String
        get() = prefs.getString("serverIp", "192.168.0.1")!!
        set(value) {
            prefs.edit().putString("serverIp", value).apply()
        }

    var serverPort: Int
        get() = prefs.getInt("serverPort", 6666)
        set(value) {
            prefs.edit().putInt("serverPort", value).apply()
        }

}


class AppConfig(
    var enableTracking: Boolean,
    var lastSavedPosition: Position,
    var timeOut: Long,
    var sensivity: Float,
    var deviceId: Int,
    var serverIp: String,
    var serverPort: Int
)