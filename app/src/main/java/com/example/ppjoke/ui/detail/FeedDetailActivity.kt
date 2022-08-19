package com.example.ppjoke.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedCommentAdapter
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.bean.UgcBean
import com.example.ppjoke.databinding.ActivityFeedDetailBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.binding_action.InteractionPresenter.checkIsLogin
import com.example.ppjoke.ui.capture.PreviewActivity
import com.example.ppjoke.ui.profile.ProfileActivity
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.CommentDialog
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.xtc.base.BaseMvvmActivity
import com.xtc.base.utils.toastShort
import kotlin.properties.Delegates

class FeedDetailActivity : BaseMvvmActivity<ActivityFeedDetailBinding, FeedDetailViewModel>() ,View.OnClickListener{
    private var adapter: FeedCommentAdapter? = null
    private var position by Delegates.notNull<Int>()
    private var feed:FeedBean?=null
    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        setToolBarTitle("帖子详情")
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel
        binding.emptyView.setTitle("还没有评论，快来抢一楼吧")
        feed = intent.getParcelableExtra<FeedBean>("KEY_FEED")
        position=intent.getIntExtra("KEY_POSITION",-1)
        binding.feed = feed
        if (feed != null) {
            binding.authorInfoLayout.layoutFeedDetailAuthorInfo.viewModel=mViewModel
            binding.authorInfoLayout.layoutFeedDetailAuthorInfo.btnFollow.visibility= if(feed!!.author!!.userId== MMKVUtils.getInstance().getUserId()) View.GONE else View.VISIBLE
            binding.headerImage.bindData(feed!!.width ?: 0, feed!!.height ?: 0, 16, feed!!.cover)
            feed!!.itemId?.let { mViewModel?.getCommentList(itemId = it)
            }
            feed!!.author!!.userId?.let { mViewModel!!.getUserRelation(it) }
        }
        binding.refreshLayout.apply {
            setRefreshFooter(BallPulseFooter(context))
            setOnLoadMoreListener {
                if (adapter?.data?.size!! > 0 && adapter?.data?.size!! % 10 == 0) {
                    adapter?.data?.last()?.let {
                        it.id?.let { it1 -> mViewModel?.loadMore(it1, it.itemId!!) }
                    }
                }
                finishLoadMore(2000)
            }
        }

        binding.authorInfoLayout.layoutFeedDetailAuthorInfo.authorAvatar.setOnClickListener(this)
        binding.authorInfoLayout.layoutFeedDetailAuthorInfo.btnFollow.setOnClickListener(this)
        binding.interactionLayout.inputView.setOnClickListener(this)
        binding.interactionLayout.btnLike.setOnClickListener(this)
        binding.interactionLayout.btnCollect.setOnClickListener(this)
        binding.interactionLayout.btnShare.setOnClickListener(this)
        binding.headerImage.setOnClickListener(this)
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.commentList.observe(this) {
            if (it.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
                binding.refreshLayout.setEnableLoadMore(false)
                binding.refreshLayout.setEnableRefresh(false)
            }
            if (adapter == null) {
                adapter = FeedCommentAdapter(ArrayList(),feed!!.author!!.userId!!)
                adapter!!.addData(it)
                adapter!!.addChildClickViewIds(R.id.comment_like)
                adapter!!.addChildClickViewIds(R.id.comment_author_avatar)
                adapter!!.addChildClickViewIds(R.id.comment_cover)
                adapter!!.addChildClickViewIds(R.id.comment_delete)
                adapter!!.setOnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.comment_like -> {
                            if(checkIsLogin(this,mViewModel!!.userId)) {
                                mViewModel?.commentLike(adapter!!.data[position].commentId!!)
                                adapter!!.data[position].ugc!!.hasLiked = mViewModel!!.isLike
                                if (mViewModel!!.isLike) {
                                    adapter!!.data[position].ugc!!.likeCount =
                                        adapter!!.data[position].ugc!!.likeCount!!.plus(
                                            1
                                        )
                                } else {
                                    adapter!!.data[position].ugc!!.likeCount =
                                        adapter!!.data[position].ugc!!.likeCount?.minus(
                                            1
                                        )
                                }
                                adapter!!.notifyItemChanged(position)
                            }
                        }
                        R.id.comment_author_avatar -> {
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("USERID", adapter!!.data[position].userId)
                            myActivityLauncher.launch(intent)
                        }
                        R.id.comment_cover->{
                            val isVideo=adapter!!.data[position].commentType==3
                            val url=if(isVideo) adapter!!.data[position].videoUrl else adapter!!.data[position].imageUrl
                            PreviewActivity.startActivityForResult(this,url,isVideo,null)
                        }
                        R.id.comment_delete->{
                            val popView=XPopup.Builder(this)
                                .hasNavigationBar(false)
                                .isDestroyOnDismiss(true)
                                .dismissOnTouchOutside(false)
                                .asConfirm("提示","确定要删除该条评论？") {
                                    if(checkIsLogin(this,mViewModel!!.userId)) {
                                        mViewModel!!.deleteComment(
                                            adapter!!.data[position].itemId!!,
                                            adapter!!.data[position].commentId!!
                                        ) { isSuccess ->
                                            if (isSuccess) {
                                                adapter!!.data.removeAt(position)
                                                adapter!!.notifyItemRemoved(position)
                                            } else {
                                                toastShort("出错了，删除失败！")
                                            }
                                        }
                                    }

                                }
                            popView.show()

                        }
                    }
                }
                binding.recyclerView.layoutManager = object : LinearLayoutManager(this) {
                    override fun canScrollVertically(): Boolean {
                        return false   //禁止recycleView滑动
                    }
                }
                binding.recyclerView.adapter = adapter
            } else {
                adapter!!.setNewInstance(it.toMutableList())
            }
        }
        mViewModel!!.loadMoreList.observe(this) {
            adapter!!.addData(it)
        }

    }

    override fun getViewBinding(): ActivityFeedDetailBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_feed_detail)
    }

    override fun getViewModel(): FeedDetailViewModel? {
        return ViewModelProvider(this).get(FeedDetailViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_like->{
                InteractionPresenter.toggleFeedLikeInternal(feed!!,this)
            }
            R.id.btn_collect->{
                InteractionPresenter.toggleFeedFeedFavorite(feed!!,this)
            }
            R.id.btn_share->{
                InteractionPresenter.openShare(this,feed!!)
            }
            R.id.input_view->{
                val commentDialog = CommentDialog()
                commentDialog.itemId = feed?.itemId
                commentDialog.show(supportFragmentManager, "commentDialog")
            }
            R.id.author_avatar->{
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USERID", binding.feed?.authorId)
                myActivityLauncher.launch(intent)
            }
            R.id.btn_follow->{
                val isFollow = InteractionPresenter.toggleFollowUser(feed?.author?.userId!!,this)
                mViewModel!!.userRelation.value=isFollow

            }
            R.id.header_image->{
                if(feed!=null) {
                    PreviewActivity.startActivityForResult(this, feed!!.cover, false, null)
                }
            }
        }
        if(v?.id==R.id.btn_like||v?.id==R.id.btn_collect){
            val intent = Intent().apply {
                putExtra("KEY_UGC",feed?.ugc)
                putExtra("KEY_POSITION",position)
            }
            setResult(Activity.RESULT_OK,intent)
        }
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val backFollow= activityResult.data?.getBooleanExtra("KEY_FOLLOW",false)
            mViewModel?.userRelation?.value=backFollow
        }
    }
}