package com.donteco.cubicmotion

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class CustomViewModelFactory(private val dataHolder: DataHolder) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(dataHolder) as T
    }
}

class Position(
    var AccPosition: Array<Float>?,
    var GyroPosition: Triple<Float, Float, Float>?
)


class BindingAdapters {

    companion object {
        @BindingAdapter("android:text")
        @JvmStatic
        fun floatToText(view: TextView, value: Float) {
            view.text = value.toString()
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun intToText(view: EditText, value: Int) {
            if (view.text.toString() != value.toString())
                view.setText(value.toString())
        }

        @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
        @JvmStatic
        fun textToInt(view: EditText): Int {
            return try {
                view.text.toString().toInt()
            } catch (ex: NumberFormatException) {
                0
            }
        }

//        @BindingAdapter("selectedValue")
//        @JvmStatic
//        fun Spinner.setSelectedValue(selectedValue: Int?) {
//            setSpinnerValue(selectedValue)
//        }
//
////        @BindingAdapter("selectedValueAttrChanged")
////        fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
////            setSpinnerInverseBindingListener(inverseBindingListener)
////        }
//
//
//        @JvmStatic
//        @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
//        fun Spinner.getSelectedValue(): Int? {
//            return getSpinnerValue()
//        }
    }


}

