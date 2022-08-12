package com.xtc.base

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.xtc.base.utils.toastShort

/**
 * 普通基类
 */
abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        onPreSuperCreate()
        super.onCreate(savedInstanceState)
        afterOnCreate()
        initPage(savedInstanceState)
    }


    /**
     * 初始化界面
     */
    protected open fun initPage(savedInstanceState: Bundle?) {
        setContentView(getLayoutId())

    }


    abstract fun getLayoutId(): Int

    open fun afterOnCreate() {}

    open fun onPreSuperCreate() {}

    /**
     * 扫描后的回调
     */
    open fun onScanResult(msg: String?) {}

    open fun onError(msg: String) {
        toastShort(msg)
    }

    open fun startActivity(clz: Class<*>) {
        startActivity(Intent(this, clz))
    }

    open fun setToolBarTitle(title: String?) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = ""
        (findViewById<View>(R.id.tvBarTitle) as TextView).text = title
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    open fun setToolBarTitleNoBack(title: String?) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = ""
        (findViewById<View>(R.id.tvBarTitle) as TextView).text = title
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }




    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    var loadingDialog: LoadingPopupView? = null

    fun showLoadingDialog() {

        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(this)
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


}