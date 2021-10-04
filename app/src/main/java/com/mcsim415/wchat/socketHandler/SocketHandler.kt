package com.mcsim415.wchat.socketHandler

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.net.Socket
import com.mcsim415.wchat.crypto.DHExchange
import java.io.IOException
import java.io.Serializable

abstract class SocketHandler: Serializable {
    private var clientSocket: Socket? = null
    private var passwordOrg: String? = null
    private var socketIO: SocketIO? = null

    abstract fun start(): Boolean

    abstract fun getAddress(): String

    @Throws(IOException::class)
    abstract fun prepareChat(dhe: DHExchange): Boolean

    fun showError(context: Context, text: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(text)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Ok"
        ) { dialog, _ -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        const val ProtocolVersion = 12098713159091807 // 17 RANDOM
    }
}