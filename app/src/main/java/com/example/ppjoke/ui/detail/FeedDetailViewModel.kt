package com.example.ppjoke.ui.detail

import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.CommentBean
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedDetailRepo
import com.xtc.base.BaseViewModel
import kotlinx.coroutines.runBlocking

class FeedDetailViewModel:BaseViewModel() {
     private val repo by lazy { FeedDetailRepo() }
     val commentList=MutableLiveData<List<CommentBean>>()
     val loadMoreList=MutableLiveData<List<CommentBean>>()
     var filePath=MutableLiveData<String>()
     val isVideo=MutableLiveData(false)
     val commentVideoImageAlpha=MutableLiveData(255)
     var isLike=false

    fun getCommentList(id:Long=0,itemId:Long,isLoadMore:Boolean=false){
        isLoading.value=true
        launch {
            val response=repo.queryCommentList(id,itemId)
            if(isLoadMore){
             loadMoreList.value=response.data
            }else {
                commentList.value = response.data
            }
            isLoading.value=false
        }
    }

    fun commentLike(commentId:Long){
        runBlocking {
                val response=repo.commentLike(1581251163,commentId)
                isLike=response.data.hasLiked
        }

    }

    fun addComment(itemId: Long,commentText:String){
        isLoading.value=true
        launch {
            repo.addComment(itemId, commentText)
            isLoading.value=false
        }
    }

    fun loadMore(id: Long, itemId:Long){
        getCommentList(id,itemId,true)
    }
}