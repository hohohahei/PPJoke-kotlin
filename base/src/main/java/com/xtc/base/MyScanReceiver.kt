package com.xtc.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.xtc.base.utils.LogUtils


/**
 * 扫码后监听的广播
 */
class MyScanReceiver : BroadcastReceiver() {
    private var message: OnScanListener? = null
    private var str: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        str = intent.extras!!.getString("barcode_string")
        if (TextUtils.isEmpty(str)) {
            str = intent.extras!!.getString("com.symbol.datawedge.data_string")
        }
        if (str.isNullOrEmpty()) {
            str = intent.extras!!.getString("text")
        }
        message!!.setMsg(str?.trim())

        LogUtils.d("接收到扫码的消息${str}")

    }

    fun setOnMessageListener(message: OnScanListener?) {
        this.message = message
    }

    interface OnScanListener {
        fun setMsg(msg: String?)
    }
}