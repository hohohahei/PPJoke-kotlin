package com.xtc.base.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

/**
 * 数据层基类
 */
open class BaseRepository {

    suspend fun <T : Any> request(call: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
        }
    }

}