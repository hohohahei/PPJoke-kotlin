package com.xtc.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xtc.base.http.ApiException
import com.xtc.base.model.BaseBean
import kotlinx.coroutines.launch

/**
 * 基类viewModel
 */
abstract class BaseViewModel : ViewModel(), LifecycleObserver{


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
            networkError.value = ApiException(baseBean.message?:"",baseBean.status)
        }


    }


    /**
     * 利用viewModelScope 来加载
     */
    protected fun <T> launch(block: suspend () -> T) {
        viewModelScope.launch {
            block()
        }
    }

}