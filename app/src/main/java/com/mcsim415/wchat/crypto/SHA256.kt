package com.mcsim415.wchat.crypto

import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SHA256 {
    fun encrypt(text: String): String {
        return encrypt(text.toByteArray())
    }

    private fun encrypt(data: ByteArray?): String {
        val md: MessageDigest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            print("Couldn't Find SHA-256 Instance.")
            return ""
        }
        md.update(data!!)
        return bytesToHex(md.digest())
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }
}