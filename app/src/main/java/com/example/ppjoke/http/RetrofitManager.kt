package com.example.ppjoke.http

import com.blankj.utilcode.util.LogUtils
import com.example.ppjoke.http.ApiUrl.BASE_URL
import com.xtc.base.http.ApiServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitManager {

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

    val getApi: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager().getRetrofit().create(ApiService::class.java)
    }

    private var mRetrofit: Retrofit? = null

    private val mOkHttpClient by lazy {
        OkHttpManager.mInstance.getHttpClient() }

    private fun getRetrofit(): Retrofit {

        if (mRetrofit == null) {

            mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        return mRetrofit!!

    }


}
