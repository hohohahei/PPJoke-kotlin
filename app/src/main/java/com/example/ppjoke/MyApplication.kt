package com.example.ppjoke

import android.app.Application
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.example.ppjoke.utils.MMKVUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.logger.DefaultLogger
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV

class MyApplication:Application() {


    override fun onCreate() {
        super.onCreate()
        XPopup.setPrimaryColor(ContextCompat.getColor(this, R.color.color_theme))
        Utils.init(this)
        MMKV.initialize(this)
        MMKVUtils.getInstance().putUserId(1581251163)  //因为登录功能还没实现，暂时先存个userId用
        LiveEventBus
            .config()
            .lifecycleObserverAlwaysActive(true) /*配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
                                                       true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
                                                       false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，
                                                       需等到Activity重新变成激活状态，方可收到消息*/
            .autoClear(true) //配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
//            .enableLogger(true) //配置是否打印日志（默认true）
//            .setLogger(DefaultLogger()) //配置Logger（默认使用DefaultLogger）
//            .setContext(applicationContext) //如果广播模式有问题，请手动传入Context，需要在application onCreate中配置
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