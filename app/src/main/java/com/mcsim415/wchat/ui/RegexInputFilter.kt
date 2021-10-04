package com.mcsim415.wchat.ui

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class RegexInputFilter(regex: String): InputFilter {
    private var mPattern: Pattern? = null
    init {
        this.mPattern = Pattern.compile(regex)
    }

    override fun filter(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence? {
        val matcher = mPattern!!.matcher(p3.toString()+p0.toString())
        return if (!matcher.matches()) "" else null
    }
}