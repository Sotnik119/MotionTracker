package com.donteco.cubicmotion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.donteco.cubicmotion.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val currentPosition: Position = Position(null, null)
    lateinit var dataHolder: DataHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataHolder = DataHolder(this)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val viewmodel = ViewModelProviders.of(this, CustomViewModelFactory(dataHolder))
            .get(MainActivityViewModel::class.java)

        binding.model = viewmodel

        binding.lifecycleOwner = this


        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val accListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, acc: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                currentPosition.AccPosition =
                    event.values.asList().subList(0, 3).toTypedArray()
                viewmodel.currentPosition.value = currentPosition

            }
        }
        sensorManager.registerListener(
            accListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )


        binding.btnCalibrate.setOnClickListener {
            try {
                viewmodel.saveData()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(this, TrackingService::class.java)
                    )
                } else {
                    startService(
                        Intent(this, TrackingService::class.java)
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "ERROR!!!!", Toast.LENGTH_LONG).show()
            }
        }



        class StatusBroadcastReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.getBooleanExtra("Status", false)?.let {
                    viewmodel.indicatorState.value = it
                }
            }
        }

        val statusIntentFilter = IntentFilter(BROADCAST_ACTION)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(StatusBroadcastReceiver(), statusIntentFilter)

        hideSystemUI()

    }

}



