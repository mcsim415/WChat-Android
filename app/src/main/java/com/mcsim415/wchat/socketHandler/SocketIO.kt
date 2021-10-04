package com.mcsim415.wchat.socketHandler

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets


class SocketIO(private val socket: Socket) {
    private var receiver: InputStream? = null
    private var sender: OutputStream? = null
    fun setupStream(): Boolean {
        try {
            receiver = socket.getInputStream()
            sender = socket.getOutputStream()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    @Throws(IOException::class)
    fun receive(): String {
        return try {
            var data = ByteArray(4)
            receiver!!.read(data, 0, 4)
            val b = ByteBuffer.wrap(data)
            b.order(ByteOrder.LITTLE_ENDIAN) // little_endian
            val length = b.int
            data = ByteArray(length)
            receiver!!.read(data, 0, length)
            String(data, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            throw e
        } catch (e: Throwable) {
            print("Socket Receive Fail.")
            ""
        }
    }

    @Throws(IOException::class)
    fun send(msg: String) {
        val data = msg.toByteArray()
        val b = ByteBuffer.allocate(4)
        b.order(ByteOrder.LITTLE_ENDIAN)
        b.putInt(data.size)
        sender!!.write(b.array(), 0, 4)
        sender!!.write(data)
    }

    fun close() {
        try {
            socket.close()
        } catch (ignored: IOException) {
        }
    }
}