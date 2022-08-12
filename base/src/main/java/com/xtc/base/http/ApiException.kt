package com.xtc.base.http

/**
 * 封装错误信息
 */
class ApiException(val errorMessage: String, val errorCode: Int) : Throwable()