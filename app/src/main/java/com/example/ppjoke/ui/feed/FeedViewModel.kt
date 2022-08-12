package com.example.ppjoke.ui.feed

import androidx.databinding.ObservableInt
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.ui.home.HomeViewModel

class FeedViewModel:HomeViewModel() {
    private val repo by lazy { FeedRepo() }
    var scrollToPosition = ObservableInt()

    fun getProfileFeeds(userId:Long,profileType:String,isLoadMore: Boolean = false,feedId: Int = 0){
        isLoading.value=true
        launch {
            val response=repo.queryProfileFeeds(userId, profileType)
            if (isLoadMore) {
                loadMoreList.value = response.data
            } else {
                feedList.value=response.data
            }
            isLoading.value = false
        }
    }

    fun getUserBehaviorList(userId: Long,behavior:Int,isLoadMore: Boolean = false,feedId: Int = 0){
        isLoading.value=true
        launch {
            val response=repo.queryUserBehaviorList(userId, behavior)
            if (isLoadMore) {
                loadMoreList.value = response.data
            } else {
                feedList.value=response.data
            }
            isLoading.value = false
        }
    }

    fun  toggleFeedLikeInternal(feed: FeedBean){
        isLoading.value=true
        launch {
            val response= repo.toggleFeedLike(1581251163,feed.itemId!!)
            if (response.data.hasLiked){
                feed.ugc.hasLiked =true
            }
        }
    }

    fun loadMoreProfileFeeds(userId:Long,profileType:String,feedId:Int){
        getProfileFeeds(userId, profileType,true,feedId)
    }

    fun loadMoreBehaviorList(userId:Long,behavior:Int,feedId:Int){
        getUserBehaviorList(userId, behavior,true,feedId)
    }


}