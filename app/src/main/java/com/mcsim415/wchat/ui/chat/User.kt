package com.mcsim415.wchat.ui.chat

class User (
    private var ip: String,
    private var name: String
) {
    fun getIp(): String {
        return ip
    }

    fun getName(): String {
        return name
    }
}
