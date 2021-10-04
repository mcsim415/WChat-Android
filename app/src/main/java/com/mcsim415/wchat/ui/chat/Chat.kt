package com.mcsim415.wchat.ui.chat

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Chat (
    private var message: String,
    private var sender: User,
    private var type: Int,
    private var createdAt: Long
    ) {

    fun getMessage(): String {
        return message
    }

    fun getSender(): User {
        return sender
    }

    fun getType(): Int {
        return type
    }

    @SuppressLint("SimpleDateFormat")
    fun getCreatedAt(): CharSequence? {
        val dateFormat = SimpleDateFormat("aa hh:mm")
        return dateFormat.format(createdAt)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCreatedAt(pattern: String): CharSequence? {
        val dateFormat = SimpleDateFormat(pattern)
        return dateFormat.format(createdAt)
    }

    companion object {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
