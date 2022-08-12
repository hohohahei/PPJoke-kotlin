package com.xtc.base.http

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

private const val TAG = "myLog"

class OkHttpManager {

    companion object {

        val mInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpManager()
        }
    }

    private var okHttpClient: OkHttpClient? = null

    // 连接超时时间
    private val connectionTime: Long = 10L

    // 写入超时时间
    private val writeTime: Long = 10L

    // 读取超时时间
    private val readTime: Long = 30L

    //    // 缓存文件
//    private val cacheFile: File = File(TApp.instance.cacheDir.absolutePath)
    // 缓存文件大小
    private val maxSize: Long = 8 * 1024 * 1024
    // OkHttpCache
//    private var cache: Cache

    fun getHttpClient(): OkHttpClient {

        if (okHttpClient == null) {

            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(connectionTime, TimeUnit.SECONDS)
                .writeTimeout(writeTime, TimeUnit.SECONDS)
                .readTimeout(readTime, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(false)
                .addInterceptor(HttpLoggingInterceptor {
                        message -> Log.d(TAG, message) }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()


        }


        return okHttpClient!!


    }

}