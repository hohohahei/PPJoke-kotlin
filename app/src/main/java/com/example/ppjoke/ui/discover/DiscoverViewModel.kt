package com.example.ppjoke.ui.discover

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.CommentBean
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.repo.DiscoverRepo
import com.example.ppjoke.ui.couch.CouchViewModel
import com.example.ppjoke.ui.home.HomeViewModel
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel
import kotlinx.coroutines.runBlocking

class DiscoverViewModel : HomeViewModel() {
    private val repo by lazy{DiscoverRepo()}
    val currentItem = ObservableInt()
    val tagList=MutableLiveData<List<TagBean>>()
    val tagLoadMoreList=MutableLiveData<List<TagBean>>()
    var tagHasFollow=MutableLiveData<Boolean>()

    fun getTagList(isLoadMore:Boolean=false,tagId: Int=0,tagType:String="all"){
        isLoading.value=true
        launch {
            val response=repo.queryTagList(tagType,tagId,userId?:0)
            if(isLoadMore){
                tagLoadMoreList.value=response.data
            }else {
                tagList.value=response.data
            }
            isLoading.value=false
        }
    }
    fun loadMoreTag(tagId:Int,tagType:String="all"){
        getTagList(true,tagId,tagType)
    }

    fun toggleTagFollow(tagId: Int){
        runBlocking {
            val response=repo.toggleTagFollow(userId?:0,tagId)
            tagHasFollow.value=response.data.hasFollow
        }
    }

}