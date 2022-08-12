package com.xtc.base.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {

    companion object {

        val getApi: ApiServices by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager().getRetrofit().create(ApiServices::class.java)
        }
        const val BASE_URL = ""
    }


    private var mRetrofit: Retrofit? = null

    private val mOkHttpClient by lazy { OkHttpManager.mInstance.getHttpClient() }


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