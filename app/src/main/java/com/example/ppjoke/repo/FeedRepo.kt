package com.example.ppjoke.repo

import com.example.ppjoke.http.RetrofitManager
import com.google.android.material.appbar.AppBarLayout
import com.xtc.base.http.BaseRepository

class FeedRepo:BaseRepository() {
    suspend fun queryFeedList(feedId:Int,feedType:String,pageCount:Int,userId:Long)= request {

        RetrofitManager.instance.getApi.queryFeedsList(feedType,feedId,  pageCount, userId).data()
    }

    suspend fun queryProfileFeeds(userId:Long,profileType:String,feedId: Int)=request {
        RetrofitManager.instance.getApi.queryProfileFeeds(userId,profileType,feedId).data()
    }

    suspend fun queryUserBehaviorList(userId:Long,behavior:Int,feedId: Int)=request {
        RetrofitManager.instance.getApi.queryUserBehaviorList(userId,behavior,feedId).data()
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

    suspend fun publishFeed(coverUploadUrl:String, fileUploadUrl:String, width:Int=0,
                            height:Int=0, userId: Long,tagId: Int,tagTitle:String,inputText:String, feedType: Int)=request{

         RetrofitManager.instance.getApi.feedPublish(coverUploadUrl,fileUploadUrl,width,height,
             userId,tagId,tagTitle,inputText,feedType).data()
    }
    suspend fun deleteFeed(itemId: Long)= request {
        RetrofitManager.instance.getApi.feedDelete(itemId).data()
    }

}