package com.xtc.base

import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.xtc.base.utils.toastShort

abstract class BaseFragment:Fragment() {
    var loadingDialog: LoadingPopupView? = null

    fun showLoadingDialog() {

        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(activity)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading("加载中...")
        }
        loadingDialog?.run {
            if (!isShow) {
                loadingDialog?.show()
            }
        }

    }

    fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }


    open fun onError(msg: String) {
        toastShort(msg)
    }

}