package com.example.ppjoke.ui.discover

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FeedMultAdapter
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.ActivityTagDetailBinding
import com.example.ppjoke.ui.detail.FeedDetailActivity
import com.example.ppjoke.ui.detail.FeedVideoDetailActivity
import com.example.ppjoke.ui.feed.FeedFragment
import com.example.ppjoke.ui.feed.FeedFragment.Companion.TYPE_TAG
import com.xtc.base.BaseMvvmActivity
import kotlin.properties.Delegates

class TagDetailActivity : BaseMvvmActivity<ActivityTagDetailBinding,DiscoverViewModel>() {
    private var tagBean: TagBean? = null
    private var adapter: FeedMultAdapter?=null
    private var position by Delegates.notNull<Int>()
    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner=this
        tagBean=intent.getParcelableExtra("TAGBEAN")
        position=intent.getIntExtra("KEY_POSITION",-1)
        binding.tagBean=tagBean
        setSupportActionBar(binding.toolbarTitle)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
       // tagBean?.title?.let { mViewModel?.getFeedList(feedType = it) }
        supportFragmentManager.findFragmentById(R.id.tag_detail_fragmentContainer)?.apply {
            supportFragmentManager.beginTransaction()
                .add(R.id.tag_detail_fragmentContainer,FeedFragment.newInstance(feedType = tagBean?.title, type = TYPE_TAG))
                .commit()
        }
//        binding.refreshLayout.apply {
//            setRefreshHeader(BezierRadarHeader(context))
//            setRefreshFooter(BallPulseFooter(context))
//            setOnRefreshListener {
//                mViewModel?.getFeedList()
//                finishRefresh(2000)
//            }
//            setOnLoadMoreListener {
//                if(adapter?.data?.size!! >0&&adapter?.data?.size!!%10==0) {
//                    adapter?.data?.last()?.let { it1 -> mViewModel?.loadMore(it1.id) }
//                }
//                finishLoadMore(2000)
//
//            }
//        }
        binding.headerIntro.headerFollow.setOnClickListener {
            tagBean?.tagId?.let { it1 -> mViewModel?.toggleTagFollow(it1) }
            binding.tagBean?.hasFollow=mViewModel!!.tagHasFollow.value
            val intent = Intent().apply {
                putExtra("KEY_FOLLOW",mViewModel!!.tagHasFollow.value)
                putExtra("KEY_POSITION",position)
            }
            setResult(Activity.RESULT_OK,intent)
        }
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.feedList.observe(this){
            if (adapter==null&& it.isNotEmpty()){
                adapter= FeedMultAdapter(it.toMutableList(), "tag_detail")
                if(adapter!!.data[position].itemType!=2) {
                    val intent = Intent(this, FeedDetailActivity::class.java)
                    intent.putExtra("KEY_FEED", adapter!!.data[position])
                    intent.putExtra("KEY_POSITION",position)
                    startActivity(intent)
                }else{
                    val intent=Intent(this, FeedVideoDetailActivity::class.java)
                    intent.putExtra("KEY_FEED", adapter!!.data[position])
                    intent.putExtra("KEY_POSITION",position)
                    startActivity(intent)
                }
            }
            else{
                adapter!!.addData(it)
            }
//            binding.recyclerView.layoutManager= LinearLayoutManager(this)
//            binding.recyclerView.adapter=adapter
        }
    }

    override fun getViewBinding(): ActivityTagDetailBinding {
        return  DataBindingUtil.setContentView(this,R.layout.activity_tag_detail)
    }

    override fun getViewModel(): DiscoverViewModel {
        return ViewModelProvider(this).get(DiscoverViewModel::class.java)
    }
}