package com.donteco.cubicmotion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel(dataHolder: DataHolder) : ViewModel() {

    val serverIp = MutableLiveData<String>()
    val serverPort = MutableLiveData<Int>()
    val sensitivity = MutableLiveData<Float>()
    val deviceId = MutableLiveData<Int>()
    val timeOut = MutableLiveData<Int>()
    val lastSavedPosition = MutableLiveData<Position>()
}