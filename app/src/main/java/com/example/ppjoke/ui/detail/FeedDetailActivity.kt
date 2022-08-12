package com.example.ppjoke.ui.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedCommentAdapter
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.databinding.ActivityFeedDetailBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.capture.PreviewActivity
import com.example.ppjoke.ui.profile.ProfileActivity
import com.example.ppjoke.widget.EmptyView
import com.example.ppjoke.widget.dialog.CommentDialog
import com.jeremyliao.liveeventbus.LiveEventBus
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
            binding.headerImage.bindData(feed!!.width ?: 0, feed!!.height ?: 0, 16, feed!!.cover)
            feed!!.itemId?.let { mViewModel?.getCommentList(itemId = it) }
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
                adapter = FeedCommentAdapter(ArrayList())
                adapter!!.addData(it)
                adapter!!.addChildClickViewIds(R.id.comment_like)
                adapter!!.addChildClickViewIds(R.id.comment_author_avatar)
                adapter!!.addChildClickViewIds(R.id.comment_cover)
                adapter!!.setOnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.comment_like -> {
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
                        R.id.comment_author_avatar -> {
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("USERID", adapter!!.data[position].userId)
                            startActivity(intent)
                        }
                        R.id.comment_cover->{
                            val isVideo=adapter!!.data[position].commentType==3
                            val url=if(isVideo) adapter!!.data[position].videoUrl else adapter!!.data[position].imageUrl
                            PreviewActivity.startActivityForResult(this,url,isVideo,null)
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
                InteractionPresenter.toggleFeedLikeInternal(feed!!)
            }
            R.id.btn_collect->{
                InteractionPresenter.toggleFeedFeedFavorite(feed!!)
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
                startActivity(intent)
            }
            R.id.btn_follow->{
                val isFollow = InteractionPresenter.toggleFollowUser(feed?.author?.userId!!)
                if (isFollow) {
                    binding.authorInfoLayout.layoutFeedDetailAuthorInfo.btnFollow.text = "已关注"
                } else {
                    binding.authorInfoLayout.layoutFeedDetailAuthorInfo.btnFollow.text = "关注"
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
}