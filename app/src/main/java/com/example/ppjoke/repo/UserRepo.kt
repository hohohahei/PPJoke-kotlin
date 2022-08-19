package com.example.ppjoke.repo

import com.example.ppjoke.http.RetrofitManager
import com.xtc.base.http.BaseRepository

class UserRepo: BaseRepository() {
    suspend fun queryUserInfo(userId:Long)=request {
        RetrofitManager.instance.getApi.queryUserInfo(userId).data()
    }

    suspend fun toggleUserFollowUser(followUserId:Long,userId: Long)=request {
        RetrofitManager.instance.getApi.toggleUserFollow(followUserId, userId).data()
    }

    suspend fun queryUserRelation(userId: Long,authorId:Long)=request{
        RetrofitManager.instance.getApi.queryUserRelation(userId, authorId).data()
    }

    suspend fun queryFans(userId: Long)= request {
        RetrofitManager.instance.getApi.queryFans(userId).data().data
    }

    suspend fun queryFollows(userId: Long)= request {
        RetrofitManager.instance.getApi.queryFollows(userId).data().data
    }
}