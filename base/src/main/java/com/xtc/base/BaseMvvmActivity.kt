package com.xtc.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding
import com.xtc.base.utils.MyDialogUtils
import com.xtc.base.utils.toastShort

/**
 * MVVM模式的基类
 */
abstract class BaseMvvmActivity<VB : ViewBinding?, VM : BaseViewModel> : BaseActivity(),
    LifecycleObserver {

    protected val binding: VB by lazy { getViewBinding() }

    val mViewModel: VM? by lazy { getViewModel() }


    private val CODE_LOGIN_OUT = 401


    override fun initPage(savedInstanceState: Bundle?) {
        setBinding()
        initView(savedInstanceState)
        addObserve()
    }

    protected abstract fun initView(savedInstanceState: Bundle?)

    private fun setBinding() {
//        binding= DataBindingUtil.setContentView<ViewDataBinding>(this, getLayoutId()) as VB
        setContentView(binding!!.root)


    }


    private val isShowLoginDialog = false
    private var alertDialog: AlertDialog? = null
    open fun addObserve() {
        mViewModel?.run {
            networkError.observe(this@BaseMvvmActivity) { error ->
                error?.let {

                    if (error.errorCode == CODE_LOGIN_OUT) {
                        if (alertDialog == null) {
                            alertDialog = AlertDialog.Builder(this@BaseMvvmActivity)
                                .setTitle("提示")
                                .setMessage("登录状态已失效,请重新登录")
                                .setPositiveButton(
                                    "重新登陆"
                                ) { dialog, which ->
                                    dialog.dismiss()
                                    var intent = Intent("com.xtc.jlalloy.loginAction")
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.putExtra("isLogin", true)
                                    startActivityForResult(intent, 10001)
                                }
                                .setNegativeButton(
                                    "取消"
                                ) { dialog, which -> dialog.dismiss() }
                                .setCancelable(false)
                                .create()
                        }
                        alertDialog?.show()

                    } else {
                        onError(error.errorMessage)
                    }


                }
            }

            isLoading.observe(this@BaseMvvmActivity) { isLoading ->

                if (isLoading) {
                    showLoadingDialog()
                } else {
                    dismissLoadingDialog()
                }
            }
            isSubmitSuccess.observe(this@BaseMvvmActivity) {
                if (it) {
                    onSubmitSuccess()
                }
            }


        }

    }

    override fun getLayoutId() = 0

    abstract fun getViewBinding(): VB

    abstract fun getViewModel(): VM?

    open fun onSubmitSuccess() {
        toastShort("提交成功")
        finish()
    }



    override fun onDestroy() {
        super.onDestroy()
        mViewModel?.let {
            lifecycle.removeObserver(this)
        }
    }
}