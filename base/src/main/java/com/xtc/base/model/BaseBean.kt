package com.xtc.base.model

import com.google.gson.annotations.SerializedName
import com.xtc.base.http.ApiException

/**
 * 请求返回外层类
 * @param <T>
 */
open class BaseBean<T> {


    @SerializedName("status")
    var status: Int = 0

    @SerializedName("data")
    var data: T? = null

    @SerializedName("message")
    var message: String? = ""


    fun isSuccess(): Boolean {
        return status==200||status==0
    }

    open fun data(): T {
        if (isSuccess()) {
            return data!!
        } else {
            throw ApiException(message?:"", -200)
        }

    }

    /**
     * 如果某些接口存在data为null的情况,需传入class对象
     * 生成空对象。避免后面做一系列空判断
     */
    fun data(clazz: Class<T>): T {
        if (isSuccess()) {
            if (data == null) {
                data = clazz.newInstance()
            }
            return data!!
        } else {
            throw ApiException(message?:"", -200)
        }

    }


}