package com.example.ppjoke.ui.binding_action

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.example.ppjoke.MainActivity
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.repo.UserRepo
import com.example.ppjoke.ui.login.LoginActivity
import com.example.ppjoke.ui.login.UserManager
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.BottomShareDialog
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.*

object InteractionPresenter {
    private val repo by lazy { FeedRepo() }
    private val profileRepo by lazy { UserRepo() }

    fun toggleFeedLikeInternal(feed: FeedBean,context: Context) {
        val userId=MMKVUtils.getInstance().getUserId()
        var isLike =false
        if (checkIsLogin(context,userId)){
        runBlocking {
            val response = repo.toggleFeedLike(userId?:0, feed.itemId!!)
            isLike = response.data.hasLiked
            feed.ugc.hasLiked=isLike
            if(isLike){
                feed.ugc.likeCount = feed.ugc.likeCount?.plus(1)
            }else{
                feed.ugc.likeCount = feed.ugc.likeCount?.minus(1)
            }
        }}

    }

    fun toggleFeedFeedFavorite(feed: FeedBean,context: Context){
        val userId=MMKVUtils.getInstance().getUserId()
        var isFavorite=false
        if (checkIsLogin(context,userId)) {
            runBlocking {
                val response = repo.toggleFeedFavorite(userId ?: 0, feed.itemId!!)
                isFavorite = response.data.hasFavorite
                feed.ugc.hasFavorite = isFavorite

            }
        }
    }

    fun toggleFollowUser(mUserId: Long,context: Context):Boolean{
        val userId=MMKVUtils.getInstance().getUserId()
        var isFollow=false
        if (checkIsLogin(context,userId)) {
            runBlocking {
                val response = profileRepo.toggleUserFollowUser(userId ?: 0, mUserId)
                isFollow = response.data.hasLiked
            }
            return isFollow
        }else{
            return false
        }
    }

    fun openShare(context: Context,feed: FeedBean){
        val userId=MMKVUtils.getInstance().getUserId()
        if (checkIsLogin(context,userId )) {
            var shareContent = feed.feeds_text
            if (!TextUtils.isEmpty(feed.url)) {
                shareContent = feed.url
            } else if (!TextUtils.isEmpty(feed.cover)) {
                shareContent = feed.cover
            }
            var shareDialog = BottomShareDialog(context)
            shareDialog.setShareContent(shareContent ?: "")
            shareDialog.setShareItemClickListener(View.OnClickListener {
                runBlocking {
                    val response = repo.shareFeed(feed.itemId!!)
                    feed.ugc.shareCount = response.data.count
                }
            })
            shareDialog.show()
        }
    }

    fun checkIsLogin(context: Context,userId:Long?):Boolean{
        println("这里会过？${userId} ${userId== 0L}")
        if(userId==null||userId== 0L){
            val popView=XPopup.Builder(context)
                .hasNavigationBar(false)
                .isDestroyOnDismiss(true)
                .dismissOnTouchOutside(false)
                .asConfirm("提示", "您还未登录，是否前往登录") {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            popView.show()
            return false
        }else{
            return true
        }
    }



}