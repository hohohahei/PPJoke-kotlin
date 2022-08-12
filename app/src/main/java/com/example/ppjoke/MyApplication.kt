package com.example.ppjoke

import android.app.Application
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.example.ppjoke.utils.MMKVUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV

class MyApplication:Application() {


    override fun onCreate() {
        super.onCreate()
        XPopup.setPrimaryColor(ContextCompat.getColor(this, R.color.color_theme))
        Utils.init(this)
        MMKV.initialize(this)
        MMKVUtils.getInstance().putUserId(1581251163)
        LiveEventBus
            .config()
            .lifecycleObserverAlwaysActive(true)
            .autoClear(true)
        initCrash()
    }
    private fun initCrash() {
        CrashUtils.init(
            getExternalFilesDir("logs")!!
        ) { crashInfo ->
            LogUtils.e(crashInfo)
        }
    }
}