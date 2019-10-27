package com.donteco.cubicmotion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel(private val dataHolder: DataHolder) : ViewModel() {

    val serverIp = MutableLiveData<String>(dataHolder.serverIp)
    val serverPort = MutableLiveData<Int>(dataHolder.serverPort)
    val sensitivity = MutableLiveData<Int>(dataHolder.sensivity)
    val indicatorState = MutableLiveData<Boolean?>(null)
    val currentPosition = MutableLiveData<Position>()
    val data = arrayOf("1", "2", "3", "4", "5")

    val deviceId = MutableLiveData<Int>(data.indexOf(dataHolder.deviceId.toString()))
    val timeOut = MutableLiveData<Int>(data.indexOf(dataHolder.timeOut.toString()))


    fun saveData() {
        dataHolder.serverIp = serverIp.value!!
        dataHolder.serverPort = serverPort.value!!
        dataHolder.sensivity = sensitivity.value!!
        dataHolder.lastSavedPosition = currentPosition.value!!
        dataHolder.timeOut = data[timeOut.value!!].toLong()
        dataHolder.deviceId = data[deviceId.value!!].toInt()
    }
}