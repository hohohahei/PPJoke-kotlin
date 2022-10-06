package com.example.ppjoke.ui.feed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedMultAdapter
import com.example.ppjoke.bean.UgcBean
import com.example.ppjoke.databinding.FragmentFeedBinding
import com.example.ppjoke.exoplayer.PageListPlayDetector
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.detail.FeedDetailActivity
import com.example.ppjoke.ui.detail.FeedVideoDetailActivity
import com.example.ppjoke.ui.profile.ProfileActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.xtc.base.BaseMvvmFragment
import com.xtc.base.utils.toastShort

private const val KEY_PROFILETYPE = "PROFILETYPE"
private const val KEY_FEEDTYPE = "FEEDTYPE"
private const val KEY_TYPE = "TYPE"
private const val KEY_BEHAVIOR = "BEHAVIOR"
private const val KEY_USERID="USERID"
private const val KEY_LAYOUT_TYPE="KEYLAYOUTTYPE"
class FeedFragment: BaseMvvmFragment<FragmentFeedBinding, FeedViewModel>() {
    private var playDetector:PageListPlayDetector?=null
    private var adapter: FeedMultAdapter? = null
    private var profileType: String? = null
    private var feedType: String? = null
    private var userId: Long? = null
    private var type: Int?=null
    private var behavior: Int? = null
    private var layoutType:Int?=null
    companion object {
        const val TYPE_PROFILE_FEED = 1001  //个人帖子
        const val TYPE_COLLECTION = 1002  //个人收藏
        const val TYPE_COMMENT=1003  //个人评论
        const val TYPE_HISTORY=1004  //个人历史
        const val TYPE_COUCH = 1005 //沙发页
        const val TYPE_TAG=1006 //标签页
        const val BEHAVIOR_FAVORITE = 0  //收藏
        const val BEHAVIOR_HISTORY = 1  //历史
        const val FEED_TAG_PIC = "pics" //图片
        const val FEED_TAG_VIDEO = "video" //视频
        const val FEED_TAG_TEXT = "text" //文本

        fun newInstance( profileType: String? = null, feedType: String? = null,
                        userId: Long? = null,
                        type: Int,
                        behavior: Int? = null,layoutType:Int?=null)=FeedFragment().apply {
                            arguments=Bundle().apply {
                                putString(KEY_PROFILETYPE,profileType)
                                putString(KEY_FEEDTYPE,feedType)
                                putInt(KEY_TYPE,type)
                                if (behavior != null) {
                                    putInt(KEY_BEHAVIOR,behavior)
                                }
                                if (userId != null) {
                                    putLong(KEY_USERID,userId)
                                }
                            }

        }
    }

    override fun getViewModel(): FeedViewModel {
        return ViewModelProvider(this).get(FeedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profileType=it.getString(KEY_PROFILETYPE)
            feedType=it.getString(KEY_FEEDTYPE)
            userId=it.getLong(KEY_USERID)
            behavior=it.getInt(KEY_BEHAVIOR)
            type=it.getInt(KEY_TYPE)
            layoutType=it.getInt(KEY_LAYOUT_TYPE)
        }
    }

    override fun initView() {
        binding?.viewModel = mViewModel
        binding?.lifecycleOwner = this
        playDetector=PageListPlayDetector(this,binding!!.recyclerView)
        binding!!.refreshLayout.apply {
            setRefreshHeader(BezierRadarHeader(context))
            setRefreshFooter(BallPulseFooter(context))
            setOnRefreshListener {
                when (type) {
                    TYPE_PROFILE_FEED -> mViewModel?.getProfileFeeds(userId!!, profileType!!)
                    TYPE_COLLECTION, TYPE_HISTORY -> mViewModel?.getUserBehaviorList(userId!!, behavior!!)
                    TYPE_COUCH, TYPE_TAG -> mViewModel?.getFeedList(feedType = feedType!!)
                }
                // mViewModel?.getFeedList()
            }
            setOnLoadMoreListener {
                if (adapter?.data?.size!! > 0 && adapter?.data?.size!! % 10 == 0) {
                    when (type) {
                        TYPE_PROFILE_FEED -> adapter?.data?.last()?.let { it1 ->
                            mViewModel?.loadMoreProfileFeeds(
                                userId!!,
                                profileType!!,
                                it1.id
                            )
                        }
                        TYPE_COLLECTION, TYPE_HISTORY -> adapter?.data?.last()?.let { it1 ->
                            mViewModel?.loadMoreBehaviorList(
                                userId!!,
                                behavior!!,
                                it1.id
                            )
                        }
                        TYPE_COUCH, TYPE_TAG -> adapter?.data?.last()?.let { it1 ->
                            mViewModel?.loadMore(
                                feedId = it1.id,
                                feedType = feedType!!
                            )
                        }
                    }

                }
            }
        }
        if (userId != null&&userId != 0L) {
            if (profileType != null && type == TYPE_PROFILE_FEED) {  //查询用户动态帖子
                mViewModel?.getProfileFeeds(userId!!, profileType!!)
            }
            if (behavior != null && type == TYPE_COLLECTION||type== TYPE_HISTORY) {  //查询用户收藏的帖子
                mViewModel?.getUserBehaviorList(userId!!, behavior!!)
            }
        } else {
            if (type == TYPE_COUCH||type== TYPE_TAG && feedType != null) {
                mViewModel?.getFeedList(feedType = feedType!!)
            }
        }

    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.feedList.observe(viewLifecycleOwner) {
            if(binding!!.refreshLayout.isRefreshing)  binding!!.refreshLayout.finishRefresh()
            if (it.isNotEmpty()) {
                if (adapter == null) {
                    adapter = FeedMultAdapter(it.toMutableList(), feedType ?: "feed_fragment",playDetector)
                    adapter!!.setOnItemClickListener { _, _, position ->
                        println("类型 ${adapter!!.data[position].itemType}")
                        if(adapter!!.data[position].itemType!=2) {
                            val intent = Intent(activity, FeedDetailActivity::class.java)
                            intent.putExtra("KEY_FEED", adapter!!.data[position])
                            intent.putExtra("KEY_POSITION",position)
                            myActivityLauncher.launch(intent)
                        }else{
                            val intent=Intent(activity, FeedVideoDetailActivity::class.java)
                            intent.putExtra("KEY_FEED", adapter!!.data[position])
                            intent.putExtra("KEY_POSITION",position)
                            myActivityLauncher.launch(intent)
                        }
                    }
                    adapter!!.addChildClickViewIds(R.id.avatar)
                    adapter!!.addChildClickViewIds(R.id.like)
                    adapter!!.addChildClickViewIds(R.id.favorite)
                    adapter!!.addChildClickViewIds(R.id.share)
                    adapter!!.addChildClickViewIds(R.id.feed_delete)
                    adapter!!.setOnItemChildClickListener{_,view,position->
                        when(view.id){
                            R.id.avatar->{
                                if(type!= TYPE_PROFILE_FEED&&type!=TYPE_COLLECTION) {
                                    val intent = Intent(activity, ProfileActivity::class.java)
                                    intent.putExtra("USERID", adapter!!.data[position].authorId)
                                    startActivity(intent)
                                }
                            }
                            R.id.like->{
                                 InteractionPresenter.toggleFeedLikeInternal(adapter!!.data[position],requireContext())
                            }
                            R.id.favorite->{
                               InteractionPresenter.toggleFeedFeedFavorite(adapter!!.data[position],requireContext())
                            }
                            R.id.share->{
                                InteractionPresenter.openShare(requireContext(),adapter!!.data[position])
                            }
                            R.id.feed_delete->{
                                val popView=XPopup.Builder(requireContext())
                                    .hasNavigationBar(false)
                                    .isDestroyOnDismiss(true)
                                    .hasBlurBg(true)
                                    .dismissOnTouchOutside(false)
                                    .asConfirm("提示","是否删除该条帖子") {
                                        adapter!!.data[position].itemId?.let { it1 ->
                                            mViewModel!!.feedDelete(
                                                it1

                                            ) {isSuccess->
                                                if(isSuccess) {
                                                    adapter!!.data.removeAt(position)
                                                    adapter!!.notifyItemRemoved(position)
                                                }else{
                                                    toastShort("出错了，删除失败！")
                                                }
                                            }
                                        }
                                    }
                                popView.show()
                            }
                        }

                    }
                    if(layoutType==1) {
                        binding!!.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                    }else{
                        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
                    }
                    binding!!.recyclerView.adapter = adapter
                } else {
                    adapter!!.setNewInstance(it.toMutableList())
                }

            }else{
                binding!!.recyclerView.visibility= View.GONE
                binding!!.emptyView.visibility= View.VISIBLE
                binding!!.refreshLayout.setEnableLoadMore(false)
            }
        }
        mViewModel!!.loadMoreList.observe(viewLifecycleOwner) {
            if ( binding!!.refreshLayout.isLoading) binding!!.refreshLayout.finishLoadMore()
            adapter?.addData(it)
        }
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val backUgc = activityResult.data?.getParcelableExtra<UgcBean>("KEY_UGC")
            val backPosition=activityResult.data?.getIntExtra("KEY_POSITION",-1)
            if (backUgc != null&&backPosition!=null&&backPosition>=0) {
                adapter?.data!![backPosition].ugc=backUgc
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_feed
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}