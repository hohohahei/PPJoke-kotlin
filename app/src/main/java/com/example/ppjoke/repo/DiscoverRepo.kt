package com.example.ppjoke.repo

import com.example.ppjoke.http.RetrofitManager
import com.xtc.base.http.BaseRepository


class DiscoverRepo:BaseRepository() {
    suspend fun queryTagList(tagType:String,tagId: Int,userId:Long)=request{
        RetrofitManager.instance.getApi.queryTagList(tagType=tagType,tagId=tagId, userId = userId).data()
    }

    suspend fun toggleTagFollow(userId: Long,tagId: Int)=request {
        RetrofitManager.instance.getApi.toggleTagFollow(userId, tagId).data()
    }
}