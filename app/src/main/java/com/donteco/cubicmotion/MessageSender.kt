package com.donteco.cubicmotion

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MessageSender {

    companion object {
        val TAG = "Message_Sender"

        fun sendMessage(host: String, port: Int, text: String) {
            Thread(Runnable {
                try {
                    Log.d(TAG, "Sending message: $text")
                    val s = DatagramSocket()
                    val local = InetAddress.getByName(host)
//                    val msg_length = text.length
                    val message = text.toByteArray()
                    val p = DatagramPacket(message, message.size, local, port)
                    s.send(p)
                    Log.d(TAG, "Message send")


                } catch (e: Exception) {
                    Log.d(TAG, "error  $e")
                }
            }).start()

        }

//        fun sendMessage(message: Any) {
//            val host = App.dataholder.displayIP
//            val port = App.dataholder.displayPort
//            val text = message.toJson()
//            sendMessage(host, port, text)
//        }

    }

}