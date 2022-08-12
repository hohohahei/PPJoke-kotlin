package com.example.ppjoke.ui.login

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.Utils
import com.example.ppjoke.repo.LoginRepo
import com.tencent.connect.UserInfo
import com.tencent.connect.auth.QQToken
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.xtc.base.BaseViewModel
import org.json.JSONException
import org.json.JSONObject

class LoginViewModel:BaseViewModel() {
    val repo by lazy { LoginRepo() }
    val loginStatus=MutableLiveData<Boolean>()
    val loginMessage=MutableLiveData<String>()
    val tencent = Tencent.createInstance("101794421", Utils.getApp())


    fun requestLogin(activity: Activity){
        tencent.login(activity,"all",listener)
    }

    var listener: IUiListener = object : IUiListener {
        override fun onComplete(o: Any) {
            val response = o as JSONObject
            try {
                val openid = response.getString("openid")
                val accessToken = response.getString("access_token")
                val expiresIn = response.getString("expires_in")
                val expiresTime = response.getLong("expires_time")
                tencent.openId = openid
                tencent.setAccessToken(accessToken, expiresIn)
                val qqToken = tencent.qqToken
                getUserInfo(qqToken, expiresTime, openid)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onError(uiError: UiError) {
            UserManager.get().save(null)
            loginMessage.postValue("登录失败:reason$uiError")
        }

        override fun onCancel() {
            UserManager.get().save(null)
            loginMessage.postValue("登录取消")
        }
    }

    private fun getUserInfo(qqToken: QQToken, expiresTime: Long, openid: String) {
        val userInfo = UserInfo(Utils.getApp(), qqToken)
        userInfo.getUserInfo(object : IUiListener {
            override fun onComplete(o: Any) {
                val response = o as JSONObject
                try {
                    val nickname = response.getString("nickname")
                    val figureurl2 = response.getString("figureurl_2")
                    save(nickname, figureurl2, openid, expiresTime)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onError(uiError: UiError) {
                UserManager.get().save(null)
                loginMessage.postValue("登录失败:reason$uiError")
            }

            override fun onCancel() {
                UserManager.get().save(null)
                loginMessage.postValue("登录取消")
            }
        })
    }

    fun save(nickname:String,avatar:String,openid: String,expiresTime: Long){
        launch {
            repo.userInsert(nickname,avatar,openid,expiresTime)
        }

    }


}