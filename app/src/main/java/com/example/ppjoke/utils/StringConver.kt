package com.example.ppjoke.utils

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

object StringConvert {


        @JvmStatic
        fun convertFeedUgc(count: Int): String {
            return if (count < 10000) {
                count.toString()
            } else (count / 10000).toString() + "万"
        }

        @JvmStatic
        fun convertTagFeedList(num: Int): String {
            return if (num < 10000) {
                num.toString() + "人观看"
            } else {
                (num / 10000).toString() + "万人观看"
            }
        }

        @JvmStatic
        fun convertSpannable(count: Int, intro: String): CharSequence {
            val countStr = count.toString()
            val ss = SpannableString(countStr + intro)
            ss.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0,
                countStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                AbsoluteSizeSpan(16, true),
                0,
                countStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ss.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                countStr.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return ss
        }

}
