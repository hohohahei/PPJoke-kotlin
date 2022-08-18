package com.example.ppjoke.ui.feed

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel

open class FeedViewModel:BaseViewModel() {
    private val repo by lazy { FeedRepo() }
    var scrollToPosition = ObservableInt()
    val feedList = MutableLiveData<List<FeedBean>>()
    val loadMoreList = MutableLiveData<List<FeedBean>>()
    val userId= MMKVUtils.getInstance().getUserId()

    fun getFeedList(
        feedId: Int = 0,
        feedType: String = "多彩生活",
        pageCount: Int = 10,
        userId: Long =this.userId?:0,
        isLoadMore: Boolean = false
    ) {
        println("加载了")
        isLoading.value = true
        launch {
            val response = repo.queryFeedList(feedId, feedType, pageCount, userId)
            if (isLoadMore) {
                loadMoreList.value = response.data
            } else {
                feedList.value=response.data
            }
            isLoading.value = false
        }
    }

    fun loadMore(feedId: Int,feedType: String = "多彩生活",) {
        getFeedList(feedId,feedType, isLoadMore = true)
    }


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

    fun feedDelete(itemId:Long,callback:(isSuccess:Boolean)->Unit){
        launch {
            val response=repo.deleteFeed(itemId)
            callback.invoke(response.data.result)
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