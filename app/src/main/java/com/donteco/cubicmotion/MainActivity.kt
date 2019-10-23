package com.donteco.cubicmotion

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity() {

    val currentPosition: Position = Position(null, null)
    lateinit var dataHolder: DataHolder

    lateinit var savedPosition: Position


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataHolder = DataHolder(this)

        //interface +
        device_id.setText(dataHolder.deviceId.toString())
        display_ip.setText(dataHolder.serverIp)
        display_port.setText(dataHolder.serverPort.toString())
        sens_seek_bar.progress = (100 - dataHolder.sensivity * 100).toInt()
        delay.setText(dataHolder.timeOut.toString())
        //interface -

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val accListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, acc: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                currentPosition.AccPosition =
                    event.values.asList().subList(0, 3).toTypedArray()

                ac_x.text = "%.4f".format(x)
                ac_y.text = "%.4f".format(y)
                ac_z.text = "%.4f".format(z)
            }
        }
        sensorManager.registerListener(
            accListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )


        btn_calibrate.setOnClickListener {
            //            savedPosition = Position(currentPosition.AccPosition, currentPosition.GyroPosition)
//            startTracking()
//            stopService(Intent(this, TrackingService::class.java))
            try {
                dataHolder.deviceId =
                    if (device_id.text.isNullOrEmpty()) 0 else device_id.text.toString().toInt()
                dataHolder.serverIp = display_ip.text.toString()
                dataHolder.serverPort = display_port.text.toString().toInt()
                dataHolder.sensivity = 1F - (sens_seek_bar.progress.toFloat() / 100)
                dataHolder.timeOut = delay.text.toString().toLong()

                dataHolder.lastSavedPosition = currentPosition
                startService(
                    Intent(this, TrackingService::class.java)
                        .putExtra(
                            "newStartPoint",
                            currentPosition.toJson()
                        )
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "ERROR!!!!", Toast.LENGTH_LONG).show()
            }


        }


    }


}

