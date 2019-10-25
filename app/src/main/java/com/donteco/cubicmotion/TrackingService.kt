package com.donteco.cubicmotion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class TrackingService : Service() {

    lateinit var dataHolder: DataHolder
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var trackingProcessor: TrackingProcessor
    lateinit var broadcaster: LocalBroadcastManager
    lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var notificationManager: NotificationManager? = null


    override fun onCreate() {
        Log.d("Service", "Start")
        super.onCreate()
        dataHolder = DataHolder(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        broadcaster = LocalBroadcastManager.getInstance(this)

        trackingProcessor = TrackingProcessor(dataHolder, broadcaster)
        sensorManager.registerListener(
            trackingProcessor.accelerateListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("Service", "onStartCommand")
        try {
            sendNotification()

            intent.extras?.getString("newStartPoint")?.let {
                val pos = it.fromJson<Position>()
                pos?.let { position ->
                    trackingProcessor.savedPosition = position
                }
            }

            trackingProcessor.restart()

            wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "MotionTracker::MotionWakelockTag"
                ).apply {
                    acquire()
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("Service", "Stop")
        trackingProcessor.stopTracking()
        wakeLock.release()
        super.onDestroy()
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(id, name, importance)

            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as? NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }

    }

    private fun sendNotification() {
        createNotificationChannel(
            "positionTrackingSystem",
            "NotifyAboutTracking",
            "tracking position channel"
        )

        val notificationID = 105
        val channelID = "positionTrackingSystem"
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            this, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(
            this,
            channelID
        )
            .setContentTitle("Cubic Tracking System")
            .setContentText("Service is working.")
            .setContentIntent(resultPendingIntent)
            .setSmallIcon(R.drawable.ic_notify)
            .setChannelId(channelID)
            .setAutoCancel(true)
            .build()
        startForeground(notificationID, notification)

    }
}