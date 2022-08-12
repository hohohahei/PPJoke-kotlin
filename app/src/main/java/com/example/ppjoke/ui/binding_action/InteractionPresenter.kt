package com.example.ppjoke.ui.binding_action

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.repo.UserRepo
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.BottomShareDialog
import kotlinx.coroutines.*

object InteractionPresenter {
    private val repo by lazy { FeedRepo() }
    private val profileRepo by lazy { UserRepo() }
    private val userId=MMKVUtils.getInstance().getUserId()


    fun toggleFeedLikeInternal(feed: FeedBean) {
        var isLike =false
        runBlocking {
            val response = repo.toggleFeedLike(userId?:0, feed.itemId!!)
            isLike = response.data.hasLiked
            feed.ugc.hasLiked=isLike
            if(isLike){
                feed.ugc.likeCount = feed.ugc.likeCount?.plus(1)
            }else{
                feed.ugc.likeCount = feed.ugc.likeCount?.minus(1)
            }
        }

    }

    fun toggleFeedFeedFavorite(feed: FeedBean){
        var isFavorite=false
        runBlocking {
            val response= repo.toggleFeedFavorite(userId?:0,feed.itemId!!)
             isFavorite=response.data.hasFavorite
            feed.ugc.hasFavorite=isFavorite

        }
    }

    fun toggleFollowUser(mUserId: Long):Boolean{
        var isFollow=false
        runBlocking {
            val response= profileRepo.toggleUserFollowUser(userId?:0,mUserId)
            isFollow=response.data.hasLiked
        }
        return isFollow
    }

    fun openShare(context: Context,feed: FeedBean){
        var shareContent=feed.feeds_text
        if(!TextUtils.isEmpty(feed.url)){
            shareContent=feed.url
        }else if (!TextUtils.isEmpty(feed.cover)){
            shareContent=feed.cover
        }
        var shareDialog=BottomShareDialog(context)
        shareDialog.setShareContent(shareContent?:"")
        shareDialog.setShareItemClickListener(View.OnClickListener {
            runBlocking {
                val response= repo.shareFeed(feed.itemId!!)
                feed.ugc.shareCount =response.data.count
            }
        })
        shareDialog.show()
    }



}