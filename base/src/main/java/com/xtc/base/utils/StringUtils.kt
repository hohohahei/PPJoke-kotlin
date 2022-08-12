package com.xtc.base.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**String 扩展方法**/


/**
 * md5
 */
fun String.toMd5(): String {
    return if (this.isEmpty()) ""
    else {
        try {
            val md5: MessageDigest = MessageDigest.getInstance("MD5")
            val bytes: ByteArray = md5.digest(this.toByteArray())
            return with(StringBuilder()) {
                bytes.forEach {
                    val temp: String = Integer.toHexString(it.toInt() and 0xff)
                    if (temp.length == 1) {
                        append("0").append(temp)
                    } else {
                        append(temp)
                    }
                }
                toString()
            }
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
    }
}

/**
 * retrofit的json body
 */
fun String.toRequestBody(): RequestBody {
    return this.toRequestBody("application/json;charset=UTF-8".toMediaTypeOrNull())
}