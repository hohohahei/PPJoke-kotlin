package com.example.ppjoke.ui.couch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ppjoke.R
import com.example.ppjoke.databinding.FragmentCouchBinding
import com.example.ppjoke.ui.discover.FocusOnFragment
import com.example.ppjoke.ui.discover.RecommendFragment
import com.example.ppjoke.ui.feed.FeedFragment
import com.example.ppjoke.ui.feed.FeedFragment.Companion.FEED_TAG_PIC
import com.example.ppjoke.ui.feed.FeedFragment.Companion.FEED_TAG_TEXT
import com.example.ppjoke.ui.feed.FeedFragment.Companion.FEED_TAG_VIDEO
import com.example.ppjoke.ui.feed.FeedFragment.Companion.TYPE_COUCH
import com.example.ppjoke.ui.my.MyFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.xtc.base.BaseMvvmFragment

class CouchFragment :  BaseMvvmFragment<FragmentCouchBinding,CouchViewModel>(){
    private val titles = arrayOf("视频","图片","文本")
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun getViewModel(): CouchViewModel {
        return ViewModelProvider(this).get(CouchViewModel::class.java)
    }

    override fun initView() {
        binding?.lifecycleOwner=this
        fragmentList.add(FeedFragment.newInstance(feedType = FEED_TAG_VIDEO, type = TYPE_COUCH))
        fragmentList.add(FeedFragment.newInstance(feedType = FEED_TAG_PIC, type = TYPE_COUCH))
        fragmentList.add(FeedFragment.newInstance(feedType = FEED_TAG_TEXT, type = TYPE_COUCH))
        binding!!.viewPagerCouch.adapter=object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return  fragmentList.size
            }
            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0->FeedFragment.newInstance(feedType = FEED_TAG_VIDEO, type = TYPE_COUCH)
                    1->FeedFragment.newInstance(feedType = FEED_TAG_PIC, type = TYPE_COUCH)
                    2->FeedFragment.newInstance(feedType = FEED_TAG_TEXT, type = TYPE_COUCH)
                    else -> FeedFragment()
                }
            }

        }
        binding!!.viewPagerCouch.offscreenPageLimit=2
        val tabLayoutMediator = TabLayoutMediator(
            binding!!.tabLayout, binding!!.viewPagerCouch
        ) { tab, position -> tab.text = titles[position] }
        tabLayoutMediator.attach()

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_couch
    }
}