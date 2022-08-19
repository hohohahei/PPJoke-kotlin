package com.example.ppjoke.ui.detail

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.CommentBean
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedDetailRepo
import com.example.ppjoke.ui.my.MyViewModel
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel
import kotlinx.coroutines.runBlocking
import javax.security.auth.callback.Callback

class FeedDetailViewModel:MyViewModel() {
     private val repo by lazy { FeedDetailRepo() }
     val commentList=MutableLiveData<List<CommentBean>>()
     val loadMoreList=MutableLiveData<List<CommentBean>>()
     var filePath=MutableLiveData<String>()
     val isVideo=MutableLiveData(false)
     val commentVideoImageAlpha=MutableLiveData(255)
     var isLike=false
     var userId=MMKVUtils.getInstance().getUserId()

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
      userId?.let {
          runBlocking {
              val response = repo.commentLike(it, commentId)
              isLike = response.data.hasLiked
          }
      }

    }

    fun addComment(itemId: Long,commentText:String){
        isLoading.value=true
        launch {
            userId?.let {
                repo.addComment(itemId, commentText, it)
                isLoading.value = false
            }
        }
    }

    fun deleteComment(itemId: Long,commentId: Long,callback:(isSuccess:Boolean)->Unit){
        launch {
            userId?.let {
                var response=repo.deleteComment(itemId,commentId, it)
                callback.invoke(response.data.result)
            }

        }
    }

    fun loadMore(id: Long, itemId:Long){
        getCommentList(id,itemId,true)
    }
}