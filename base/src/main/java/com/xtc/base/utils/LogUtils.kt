package com.xtc.base.utils

import android.util.Log

object LogUtils {
    /**
     * 日志输出级别 0为不输出
     */
    private const val logMode = 6
    private const val TAG = "myLog"

    /**
     * 输出Error信息
     *
     * @param tag 异常信息标识
     * @param msg 异常信息
     */
    fun e(tag: String?, msg: String?) {
        if (tag == null || msg == null) {
            return
        }
        if (Log.ERROR <= logMode) {
            Log.e(tag, msg)
        }
    }

    /**
     * 输出警告信息
     *
     * @param tag 错误信息标识
     * @param msg 错误信息
     */
    fun w(tag: String?, msg: String?) {
        if (tag == null || msg == null) {
            return
        }
        if (Log.WARN <= logMode) {
            Log.w(tag, msg)
        }
    }

    /**
     * 输出普�?信息
     *
     * @param tag 普�?信息
     * @param msg 异常信息
     */
    fun i(tag: String?, msg: String?) {
        if (tag == null || msg == null) {
            return
        }
        if (Log.INFO <= logMode) {
            Log.i(tag, msg)
        }
    }

    /**
     * 输出debug信息
     *
     * @param tag 调试信息标识
     * @param msg 调试信息
     */
    fun d(tag: String?, content: String?) {
        if (tag == null || content == null) {
            return
        }
        if (Log.DEBUG <= logMode) {

            var content = content
            val p = 2048
            val length = content.length.toLong()
            if (length < p || length == p.toLong())
                Log.d(tag, content)
            else {
                while (content!!.length > p) {
                    val logContent = content.substring(0, p)
                    content = content.replace(logContent, "")
                    Log.d(tag, logContent)
                }
                Log.d(tag, content)
            }

        }
    }

    fun d(msg: String?) {
        if (msg == null) {
            return
        }
        if (Log.DEBUG <= logMode) {
            Log.d(TAG, "------>$msg")
        }
    }

    fun e(msg: String?) {
        if (msg == null) {
            return
        }
        if (Log.DEBUG <= logMode) {
            Log.e(TAG, msg)
        }
    }

}