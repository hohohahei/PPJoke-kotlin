package com.example.ppjoke.ui.home


import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest

open class HomeViewModel : BaseViewModel() {
    private val feedRepo by lazy { FeedRepo() }
    val feedList = MutableLiveData<List<FeedBean>>()
    val loadMoreList = MutableLiveData<List<FeedBean>>()
    val userId=MMKVUtils.getInstance().getUserId()

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
            val response = feedRepo.queryFeedList(feedId, feedType, pageCount, userId)
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


}