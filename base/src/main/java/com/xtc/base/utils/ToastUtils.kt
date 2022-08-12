package com.xtc.base.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * toast封装
 */

fun Context.toastShort(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toastShort(res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.toastLong(res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
}


fun Fragment.toastShort(msg: String) {
    Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastShort(res: Int) {
    Toast.makeText(this.context, res, Toast.LENGTH_SHORT).show()
}


fun Fragment.toastLong(msg: String) {
    Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()
}

fun Fragment.toastLong(res: Int) {
    Toast.makeText(this.context, res, Toast.LENGTH_LONG).show()
}