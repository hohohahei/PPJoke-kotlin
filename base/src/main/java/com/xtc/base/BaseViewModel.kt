package com.xtc.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtc.base.http.ApiException
import com.xtc.base.model.BaseBean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 基类viewModel
 */
abstract class BaseViewModel : ViewModel(), LifecycleObserver {


    /**
     * 加载状态
     */
    val isLoading = MutableLiveData<Boolean>()

    /**
     * 异常状态
     */
    val networkError = MutableLiveData<ApiException>()


    /**
     * 是否提交成功
     */
    var isSubmitSuccess = MutableLiveData<Boolean>()


    fun isSubmitSuccess(baseBean: BaseBean<*>) {

        if (baseBean.isSuccess()) {
            isSubmitSuccess.value = baseBean.isSuccess()
        } else {
            networkError.value = ApiException(baseBean.message ?: "", baseBean.status)
        }


    }


    /**
     * 利用viewModelScope 来加载
     */
    protected fun <T> launch(block: suspend () -> T) {
        viewModelScope.launch {
            runCatching {
                block()
            }.onFailure {
                it.printStackTrace()
                getApiException(it).apply {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        networkError.value = this@apply
                    }
                }
            }

        }
    }

    private fun getApiException(e: Throwable): ApiException {
        return when (e) {
            is UnknownHostException -> {
                ApiException("网络异常", -100)
            }
            is JSONException -> {//|| e is JsonParseException
                ApiException("数据异常", -100)
            }
            is SocketTimeoutException -> {
                ApiException("连接超时", -100)
            }
            is ConnectException -> {
                ApiException("连接错误", -100)
            }
            is ApiException -> {
                e
            }
            is CancellationException -> {
                ApiException("", -10)
            }
            is HttpException -> {

                if (e.code() == 400) {
                    //400的情况 token接口有个错误值
                    try {
                        val string = e.response()?.errorBody()?.string()
                        val jsonObject = JSONObject(string)
                        ApiException(jsonObject.getString("error_description"), e.code())
                    } catch (ignore: Exception) {
                        ApiException("网络请求出错 code ${e.code()}", e.code())
                    }
                } else {
                    ApiException("网络请求出错 code ${e.code()}", e.code())
                }

            }
            else -> {
                ApiException("未知错误", -100)
            }
        }
    }

}