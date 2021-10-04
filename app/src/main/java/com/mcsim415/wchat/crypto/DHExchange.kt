package com.mcsim415.wchat.crypto

import java.math.BigInteger
import java.security.SecureRandom


class DHExchange {
    private var p: BigInteger? = null
    private var g: BigInteger? = null
    private var privateK: BigInteger? = null
    private var finalCommonKey: BigInteger? = null
    private val bitLength = 512

    fun makeCommonKeys() {
        val rnd = SecureRandom()
        p = BigInteger.probablePrime(bitLength, rnd)
        g = BigInteger(p!!.bitLength() - 1, rnd)
    }

    fun makePrivateKey() {
        val rnd = SecureRandom()
        privateK = BigInteger(bitLength, rnd)
    }

    val privateKey: BigInteger
        get() = g!!.modPow(privateK!!, p!!)

    fun setFinalCommonKey(opk: BigInteger) {
        finalCommonKey = opk.modPow(privateK!!, p!!)
    }

    fun getFinalCommonKey(): BigInteger? {
        return if (finalCommonKey != null) finalCommonKey else null
    }

    fun setP(_p: BigInteger?) {
        p = _p
    }

    fun setG(_g: BigInteger?) {
        g = _g
    }

    fun getP(): BigInteger? {
        return if (p != null) p else null
    }

    fun getG(): BigInteger? {
        return if (g != null) g else null
    }
}