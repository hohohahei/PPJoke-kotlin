package com.example.ppjoke.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedCommentAdapter
import com.example.ppjoke.bean.FeedBean
import com.example.ppjoke.databinding.LayoutFeedDetailBottomInateractionBinding
import com.example.ppjoke.databinding.LayoutFeedDetailTypeVideoBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.capture.PreviewActivity
import com.example.ppjoke.ui.profile.ProfileActivity
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.CommentDialog
import com.lxj.xpopup.XPopup
import com.xtc.base.BaseMvvmActivity
import com.xtc.base.utils.toastShort
import kotlin.properties.Delegates

class FeedVideoDetailActivity :
    BaseMvvmActivity<LayoutFeedDetailTypeVideoBinding, FeedDetailViewModel>(),View.OnClickListener {
    private var backPressd=false
    private var adapter: FeedCommentAdapter? = null
    private lateinit var mInateractionBinding: LayoutFeedDetailBottomInateractionBinding
    private var feed:FeedBean?=null
    private var position by Delegates.notNull<Int>()
    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        feed = intent.getParcelableExtra("KEY_FEED")
        position=intent.getIntExtra("KEY_POSITION",-1)
        binding.feed = feed
        binding.emptyView.setTitle("还没有评论，快来抢一楼吧")
        mInateractionBinding=binding.bottomInteraction
        val authorInfoView=binding.authorInfo.root
        var params=authorInfoView.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior=ViewAnchorBehavior(R.id.player_view)
        val layoutParams=binding.playerView.layoutParams as CoordinatorLayout.LayoutParams
        val behavior=layoutParams.behavior as ViewZoomBehavior
        behavior.setViewZoomCallback(object :ViewZoomBehavior.ViewZoomCallback{
            override fun onDragZoom(height: Int) {
                val bottom=binding.playerView.bottom
                val moveUp=height<bottom
                val fullScreen=if(moveUp) height>=binding.coordinator.bottom-mInateractionBinding.root.height
                         else height>=binding.coordinator.bottom
                setViewAppearance(fullScreen)
            }

        })
        if (feed != null) {
            binding.playerView.bindData(
                "feed_video_detail",
                feed!!.width ?: 0,
                feed!!.height ?: 0,
                feed!!.cover,
                feed!!.url
            )
            binding.playerView.post {
                val fullscreen = binding.playerView.bottom >= binding.coordinator.bottom
                setViewAppearance(fullscreen)
            }
            binding.authorInfo.viewModel=mViewModel
            binding.fullscreenAuthorInfo.viewModel=mViewModel
            binding.authorInfo.btnFollow.visibility=if(feed!!.author!!.userId==MMKVUtils.getInstance().getUserId())View.GONE else View.VISIBLE
            binding.fullscreenAuthorInfo.btnFollow.visibility=if(feed!!.author!!.userId==MMKVUtils.getInstance().getUserId())View.GONE else View.VISIBLE
            feed!!.itemId?.let { mViewModel?.getCommentList(itemId = it) }
            feed!!.author!!.userId?.let { mViewModel!!.getUserRelation(it) }
        }
        binding.actionClose.setOnClickListener { finish() }
        binding.fullscreenAuthorInfo.authorAvatar.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USERID", feed?.authorId)
            myActivityLauncher.launch(intent)
        }
        binding.fullscreenAuthorInfo.btnFollow.setOnClickListener {
            val isFollow= InteractionPresenter.toggleFollowUser(feed?.author?.userId!!, this)
            mViewModel!!.userRelation.value=isFollow
        }
        binding.authorInfo.authorAvatar.setOnClickListener(this)

        binding.authorInfo.btnFollow.setOnClickListener(this)
        //视频底部评论评论框
        mInateractionBinding.inputView.setOnClickListener(this)
        mInateractionBinding.btnLike.setOnClickListener(this)
        mInateractionBinding.btnCollect.setOnClickListener(this)
        mInateractionBinding.btnShare.setOnClickListener(this)

    }

    private fun setViewAppearance(fullScreen: Boolean) {
        binding.fullscreen = fullScreen
        mInateractionBinding.fullscreen = fullScreen
        binding.fullscreenAuthorInfo.root.visibility = if (fullScreen) View.VISIBLE else View.GONE
        //底部互动区域的高度
        val inputHeight = mInateractionBinding.root.measuredHeight
        //底部互动区域的bottom值
        val inputBottom=mInateractionBinding.root. bottom
        //播放控制器的高度
        val ctrlViewHeight = binding.playerView.playController!!.measuredHeight
        //播放控制器的bottom值
        val bottom = binding.playerView.playController!!.bottom
        //全屏播放时，播放控制器需要处在底部互动区域的上面
        binding.playerView.playController!!.y =
            if (fullScreen) (inputBottom - inputHeight - ctrlViewHeight).toFloat() else (bottom - ctrlViewHeight).toFloat()
        mInateractionBinding.inputView.setBackgroundResource(
            if (fullScreen) R.drawable.bg_edit_view2 else R.drawable.bg_edit_view)
    }

    override fun getViewBinding(): LayoutFeedDetailTypeVideoBinding {
        return DataBindingUtil.setContentView(this, R.layout.layout_feed_detail_type_video)
    }

    override fun getViewModel(): FeedDetailViewModel {
        return ViewModelProvider(this).get(FeedDetailViewModel::class.java)
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.commentList.observe(this) {
            if (it.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
            if (adapter == null) {
                adapter = FeedCommentAdapter(ArrayList(), feed!!.author!!.userId!!)
                adapter!!.addData(it)
                adapter!!.addChildClickViewIds(R.id.comment_like)
                adapter!!.addChildClickViewIds(R.id.comment_author_avatar)
                adapter!!.addChildClickViewIds(R.id.comment_cover)
                adapter!!.addChildClickViewIds(R.id.comment_delete)
                adapter!!.setOnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.comment_like -> {
                            if(InteractionPresenter.checkIsLogin(this, mViewModel!!.userId)) {
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
                                adapter!!.notifyItemChanged(position, "commentAdd")

                            }
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
                        R.id.comment_delete->{
                            val popView= XPopup.Builder(this)
                                .hasNavigationBar(false)
                                .isDestroyOnDismiss(true)
                                .dismissOnTouchOutside(false)
                                .asConfirm("提示","确定要删除该条评论？") {
                                    if(InteractionPresenter.checkIsLogin(this, mViewModel!!.userId)) {
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
                        return true
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


    override fun onBackPressed() {
        super.onBackPressed()
        backPressd=true
        binding.playerView.playController!!.translationY=0f
    }

    override fun onPause() {
        super.onPause()
        if(!backPressd){
            binding.playerView.inActive()
        }
    }

    override fun onResume() {
        super.onResume()
        backPressd=false
        binding.playerView.onActive()
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
                val isFollow= InteractionPresenter.toggleFollowUser(feed?.author?.userId!!,this)
                mViewModel!!.userRelation.value=isFollow
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