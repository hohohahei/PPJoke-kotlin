package com.example.ppjoke.repo

import com.example.ppjoke.http.RetrofitManager
import com.xtc.base.http.BaseRepository


class FeedDetailRepo:BaseRepository() {

    suspend fun queryCommentList(id:Long=0,itemId:Long)=request {
        RetrofitManager.instance.getApi.queryFeedComments(id,itemId).data()
    }

    suspend fun commentLike(userId:Long,commentId:Long)=request {
        RetrofitManager.instance.getApi.toggleCommentLike(userId, commentId).data()
    }

    suspend fun addComment(itemId: Long,commentText:String)=request {
        RetrofitManager.instance.getApi.addComment(itemId,1581251163,commentText).data()
    }

    suspend fun deleteComment(itemId: Long,commentId: Long,userId: Long)=request {
        RetrofitManager.instance.getApi.deleteComment(itemId,commentId, userId).data()
    }
}