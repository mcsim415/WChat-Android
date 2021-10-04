package com.mcsim415.wchat.socketHandler

import android.content.Context
import com.mcsim415.wchat.crypto.DHExchange
import java.io.Serializable
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class ServerSocketHandler(private val port: String, private val context: Context): SocketHandler(), Serializable {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var socketIO: SocketIO
    private var passwordOrg: String? = null

    override fun start(): Boolean {
        try {
            serverSocket = ServerSocket()
            val isd = InetSocketAddress(port.toInt())
            serverSocket.bind(isd)
        } catch (e: Throwable) {
            return false
        }
        return true
    }

    override fun getAddress(): String {
        return clientSocket.inetAddress.toString() + ":" + clientSocket.port
    }

    fun accept(): Boolean {
        clientSocket = try {
            serverSocket.accept()
        } catch (e: Throwable) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun prepareChat(dhe: DHExchange): Boolean {
        socketIO = SocketIO(clientSocket)
        val res = socketIO.setupStream()
        if (!res) {
            return false
        }

        if (Objects.equals(socketIO.receive(), ProtocolVersion)) {
            socketIO.send("100")
        } else {
            showError(context,"Version Not Matched.")
            socketIO.send("200")
            socketIO.close()
            return false
        }

        return true
    }
}