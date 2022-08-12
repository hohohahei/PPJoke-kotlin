package com.xtc.base.utils

import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.lxj.xpopupext.listener.TimePickerListener
import com.lxj.xpopupext.popup.TimePickerPopup


class MyDialogUtils {


    companion object {


        fun showTimePickDialog(
            context: Context,
            mode: TimePickerPopup.Mode = TimePickerPopup.Mode.YMD,
            timePickerListener: TimePickerListener
        ) {
            val timePicker = TimePickerPopup(context)
                .setMode(mode)
                .setTimePickerListener(timePickerListener)

            XPopup.Builder(context)
                .asCustom(timePicker)
                .show()
        }


        /**
         * show 提示框
         */
        fun showAlertDialog(context: Context, title: String = "提示", content: String = "出错了") {
            XPopup.Builder(context)
                .asConfirm(title, content, "", "知道了", null, null, true)
                .show()

        }

        /**
         * show 确认框
         */
        fun showConfirmDialog(
            context: Context,
            content: String = "出错了",
            onConfirmListener: OnConfirmListener? = null
        ) {

            XPopup.Builder(context)
                .asConfirm("提示", content, onConfirmListener)
                .show()

        }


    }


}