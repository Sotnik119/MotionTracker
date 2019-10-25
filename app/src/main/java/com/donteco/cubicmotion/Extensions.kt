package com.donteco.cubicmotion

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

fun AppCompatActivity.hideSystemUI() {
    val decorView: View = window.decorView
    decorView.setOnSystemUiVisibilityChangeListener {
        hideSystemUI()
    }
    decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)

}


// Преобразует объект в json-строку
fun Any.toJson(): String =
    Gson().toJson(this)

// Получает объект из Json-строки
inline fun <reified T> String.fromJson(): T? =
    try {
        Gson().fromJson(this, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }


class Position(
    var AccPosition: Array<Float>?,
    var GyroPosition: Triple<Float, Float, Float>?
)