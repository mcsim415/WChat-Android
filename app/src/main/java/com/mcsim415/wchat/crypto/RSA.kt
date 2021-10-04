package com.mcsim415.wchat.crypto

import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import kotlin.math.ceil


class RSA(key: BigInteger, password_org: String) {
    private val n: BigInteger
    private var e: BigInteger? = null
    private var d: BigInteger? = null

    fun encrypt(msg: String): Array<String?> {
        val encodedMsgs = encodeMsg(msg)
        val encryptedMsgs = arrayOfNulls<String>(encodedMsgs.size)
        var i = 0
        for (encodedMsg in encodedMsgs) {
            if (encodedMsg != null) {
                if (i == 0) {
                    encryptedMsgs[i] = encodedMsg
                } else {
                    encryptedMsgs[i] = BigInteger(encodedMsg).modPow(e!!, n).toString()
                }
                i++
            }
        }
        return encryptedMsgs
    }

    fun decrypt(encryptedMsg: String?): String {
        return BigInteger(encryptedMsg!!).modPow(d!!, n).toString()
    }

    fun decodeMsg(encodedMsg: String?): String {
        return String(BigInteger(encodedMsg!!).toByteArray())
    }

    private fun encodeMsg(msg: String): Array<String?> {
        var length: Int
        var chunk = 1
        val nLen = n.toString().length - 1
        var encodedMsg = BigInteger(msg.toByteArray()).toString()
        length = encodedMsg.length
        length = if (length > nLen) {
            ceil(length / nLen.toDouble()).toInt()
        } else {
            1
        }
        length++
        val encodedMsgs = arrayOfNulls<String>(length)
        if (encodedMsg[0] == '-') {
            encodedMsgs[0] = "-"
            encodedMsg = encodedMsg.substring(1)
        } else {
            encodedMsgs[0] = "+"
        }
        var i = 0
        while (i < encodedMsg.length + 1) {
            if (i + nLen > encodedMsg.length + 1) {
                encodedMsgs[chunk] = encodedMsg.substring(i)
            } else {
                encodedMsgs[chunk] = encodedMsg.substring(i, i + nLen)
            }
            chunk++
            i += nLen
        }
        return encodedMsgs
    }

    private fun xgcd(a: BigInteger, b: BigInteger): Array<BigInteger> {
        var aP: BigInteger = a
        var bP: BigInteger = b
        val retvals = arrayOf(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        val aa = arrayOf(BigInteger.ONE, BigInteger.ZERO)
        val bb = arrayOf(BigInteger.ZERO, BigInteger.ONE)
        var q: BigInteger
        while (true) {
            q = aP.divide(bP)
            aP = aP.mod(bP)
            aa[0] = aa[0].subtract(q.multiply(aa[1]))
            bb[0] = bb[0].subtract(q.multiply(bb[1]))
            if (aP == BigInteger.ZERO) {
                retvals[0] = bP
                retvals[1] = aa[1]
                retvals[2] = bb[1]
                return retvals
            }
            q = bP.divide(aP)
            bP = bP.mod(aP)
            aa[1] = aa[1].subtract(q.multiply(aa[0]))
            bb[1] = bb[1].subtract(q.multiply(bb[0]))
            if (bP == BigInteger.ZERO) {
                retvals[0] = aP
                retvals[1] = aa[0]
                retvals[2] = bb[0]
                return retvals
            }
        }
    }

    private fun findE(totient: BigInteger): BigInteger {
        val rnd = SecureRandom()
        var randNum: BigInteger
        do {
            randNum = BigInteger(totient.bitLength(), rnd)
        } while (randNum >= totient)
        return randNum
    }

    private fun isMersenne(n: BigInteger): Boolean {
        return n.add(BigInteger.valueOf(1)).bitCount() == 1
    }

    private fun stringToSeed(s: String?): Long {
        if (s == null) {
            return 0
        }
        var hash: Long = 0
        for (c in s.toCharArray()) {
            hash = 31L * hash + c.code.toLong()
        }
        return hash
    }

    private fun makePrime(seed: String): BigInteger {
        val rnd = Random()
        rnd.setSeed(stringToSeed(seed))
        val bitLength = 2048
        return BigInteger.probablePrime(bitLength, rnd)
    }

    init {
        var p: BigInteger
        var q: BigInteger
        val sha256 = SHA256()
        p = makePrime(key.toString() + sha256.encrypt(password_org))
        q = makePrime(key.toString() + sha256.encrypt(password_org + key))
        var pi = 0
        while (isMersenne(p)) {
            p = makePrime(pi.toString() + key.toString() + sha256.encrypt(password_org))
            pi++
        }
        var qi = 0
        while (isMersenne(q)) {
            q = makePrime(qi.toString() + key.toString() + sha256.encrypt(password_org + key))
            qi++
        }
        n = p.multiply(q)
        val totient: BigInteger = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)))
        e = when {
            totient.mod(BigInteger.valueOf(65537)) != BigInteger.valueOf(0) -> {
                BigInteger.valueOf(65537)
            }
            totient.mod(BigInteger.valueOf(3)) != BigInteger.valueOf(0) -> {
                BigInteger.valueOf(3)
            }
            else -> {
                findE(totient)
            }
        }
        val gcdXY = xgcd(e!!, totient)
        d = if (gcdXY[1] < BigInteger.ZERO) {
            gcdXY[1].add(totient)
        } else {
            gcdXY[1]
        }
    }
}