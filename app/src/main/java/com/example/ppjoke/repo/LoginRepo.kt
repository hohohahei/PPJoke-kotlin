package com.example.ppjoke.repo


import com.example.ppjoke.http.RetrofitManager
import com.xtc.base.http.BaseRepository


class LoginRepo:BaseRepository() {
    suspend fun userInsert(nickName:String,avatar:String,openId:String,expiresTime:Long)= request {
        RetrofitManager.instance.getApi.userInsert(nickName, avatar, openId, expiresTime).data()
    }
}