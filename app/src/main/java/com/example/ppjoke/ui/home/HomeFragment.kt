package com.example.ppjoke.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedMultAdapter
import com.example.ppjoke.bean.UgcBean
import com.example.ppjoke.databinding.FragmentHomeBinding
import com.example.ppjoke.exoplayer.PageListPlayManager
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.detail.FeedDetailActivity
import com.example.ppjoke.ui.detail.FeedVideoDetailActivity
import com.example.ppjoke.ui.profile.ProfileActivity
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.xtc.base.BaseMvvmFragment

class HomeFragment : BaseMvvmFragment<FragmentHomeBinding,HomeViewModel>() {
    private var adapter:FeedMultAdapter?=null
    private val feedType="tab_home"
    companion object {
        fun newInstance(feedType: String?): HomeFragment {
            val args = Bundle()
            args.putString("feedType", feedType)
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(): HomeViewModel {
       return ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun initView() {
        binding!!.lifecycleOwner=this
        binding!!.viewModel=mViewModel
        binding!!.refreshLayout.apply {
             setRefreshHeader(BezierRadarHeader(context))
             setRefreshFooter(BallPulseFooter(context))
             setOnRefreshListener {
                 mViewModel?.getFeedList()
                 finishRefresh(2000)
             }
            setOnLoadMoreListener {
                if(adapter?.data?.size!! >0&&adapter?.data?.size!!%10==0) {
                    adapter?.data?.last()?.let { it1 -> mViewModel?.loadMore(it1.id) }
                }
                finishLoadMore(2000)

            }
        }
        mViewModel?.getFeedList()

    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.feedList.observe(this){
            if (it.isNotEmpty()) {
                if (adapter == null) {
                    adapter = FeedMultAdapter(it.toMutableList(), feedType)
                    adapter!!.setOnItemClickListener { _, _, position ->
                        if(adapter!!.data[position].itemType!=2) {
                            val intent = Intent(activity, FeedDetailActivity::class.java)
                            intent.putExtra("KEY_FEED", adapter!!.data[position])
                            intent.putExtra("KEY_POSITION",position)
                            myActivityLauncher.launch(intent)
                        }else{
                            val intent=Intent(activity,FeedVideoDetailActivity::class.java)
                            intent.putExtra("KEY_FEED", adapter!!.data[position])
                            intent.putExtra("KEY_POSITION",position)
                            myActivityLauncher.launch(intent)
                        }
                    }
                    adapter!!.addChildClickViewIds(R.id.avatar)
                    adapter!!.addChildClickViewIds(R.id.like)
                    adapter!!.addChildClickViewIds(R.id.favorite)
                    adapter!!.addChildClickViewIds(R.id.share)
                    adapter!!.setOnItemChildClickListener { _, view, position ->
                        when (view.id) {
                            R.id.avatar -> {
                                val intent = Intent(activity, ProfileActivity::class.java)
                                intent.putExtra("USERID", adapter!!.data[position].authorId)
                                startActivity(intent)
                            }
                            R.id.like -> {
                                 InteractionPresenter.toggleFeedLikeInternal(adapter!!.data[position])
                            }
                            R.id.favorite->{
                               InteractionPresenter.toggleFeedFeedFavorite(adapter!!.data[position])
                            }
                            R.id.share->{
                                InteractionPresenter.openShare(requireContext(),adapter!!.data[position])
                            }
                        }
                    }
                    binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding!!.recyclerView.adapter = adapter
                } else {
                    adapter!!.setNewInstance(it.toMutableList())
                }

            }

        }
        mViewModel!!.loadMoreList.observe(viewLifecycleOwner){
            adapter?.addData(it)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun onDestroy() {
        super.onDestroy()
        PageListPlayManager.release(feedType)
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            //回传的数据
            val backUgc = activityResult.data?.getParcelableExtra<UgcBean>("KEY_UGC")
            val backPosition=activityResult.data?.getIntExtra("KEY_POSITION",-1)
            if (backUgc != null&&backPosition!=null&&backPosition>=0) {
                adapter?.data!![backPosition].ugc.likeCount=backUgc.likeCount
                adapter?.data!![backPosition].ugc.hasLiked=backUgc.hasLiked
                adapter?.data!![backPosition].ugc.hasFavorite=backUgc.hasFavorite
                adapter?.data!![backPosition].ugc.shareCount=backUgc.shareCount

            }
        }
    }

}