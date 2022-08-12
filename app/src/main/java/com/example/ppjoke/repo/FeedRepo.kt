package com.example.ppjoke.repo

import com.example.ppjoke.http.RetrofitManager
import com.google.android.material.appbar.AppBarLayout
import com.xtc.base.http.BaseRepository

class FeedRepo:BaseRepository() {
    suspend fun queryFeedList(feedId:Int,feedType:String,pageCount:Int,userId:Long)= request {

        RetrofitManager.instance.getApi.queryFeedsList(feedType,feedId,  pageCount, userId).data()
    }

    suspend fun queryProfileFeeds(userId:Long,profileType:String)=request {
        RetrofitManager.instance.getApi.queryProfileFeeds(userId,profileType).data()
    }

    suspend fun queryUserBehaviorList(userId:Long,behavior:Int)=request {
        RetrofitManager.instance.getApi.queryUserBehaviorList(userId,behavior).data()
    }

    suspend fun toggleFeedLike(userId: Long,itemId:Long)=request {
        RetrofitManager.instance.getApi.toggleFeedLike(userId,itemId).data()
    }

    suspend fun toggleFeedFavorite(userId: Long,itemId:Long)=request {
        RetrofitManager.instance.getApi.toggleFeedFavorite(userId,itemId).data()
    }

    suspend fun shareFeed(itemId: Long)= request {
        RetrofitManager.instance.getApi.shareFeed(itemId).data()
    }

}