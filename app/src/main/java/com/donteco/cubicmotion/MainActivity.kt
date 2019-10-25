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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val currentPosition: Position = Position(null, null)
    lateinit var dataHolder: DataHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataHolder = DataHolder(this)

        val data = arrayOf("1", "2", "3", "4", "5")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        device_id.adapter = adapter
        delay.adapter = adapter


        // устанавливаем обработчик нажатия
//        device_id.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>, view: View,
//                position: Int, id: Long
//            ) {
//                // показываем позиция нажатого элемента
//                Toast.makeText(baseContext, "Position = $position", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(arg0: AdapterView<*>) {}
//        }

        //interface +
        device_id.setSelection(dataHolder.deviceId - 1)
        delay.setSelection((dataHolder.timeOut / 1000).toInt() - 1)
        device_id.adapter
        display_ip.setText(dataHolder.serverIp)
        display_port.setText(dataHolder.serverPort.toString())
        sens_seek_bar.progress = (100 - dataHolder.sensivity * 100).toInt()
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
            try {
                dataHolder.deviceId = (device_id.selectedItemId + 1).toInt()

                dataHolder.serverIp = display_ip.text.toString()
                dataHolder.serverPort = display_port.text.toString().toInt()
                dataHolder.sensivity = 1F - (sens_seek_bar.progress.toFloat() / 100)
                dataHolder.timeOut = (delay.selectedItemId+1)*1000

                dataHolder.lastSavedPosition = currentPosition

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(this, TrackingService::class.java)
                            .putExtra(
                                "newStartPoint",
                                currentPosition.toJson()
                            )
                    )
                } else {
                    startService(
                        Intent(this, TrackingService::class.java)
                            .putExtra(
                                "newStartPoint",
                                currentPosition.toJson()
                            )
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
                    setIndicator(it)
                }
            }
        }

        val statusIntentFilter = IntentFilter(BROADCAST_ACTION)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(StatusBroadcastReceiver(), statusIntentFilter)

        hideSystemUI()

    }

    fun setIndicator(pos: Boolean) {
        indicator.setBackgroundColor(
            if (pos)
                resources.getColor(R.color.green)
            else
                resources.getColor(R.color.red)
        )
    }

}



