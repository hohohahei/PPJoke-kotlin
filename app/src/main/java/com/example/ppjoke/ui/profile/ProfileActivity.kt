package com.example.ppjoke.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ppjoke.R
import com.example.ppjoke.databinding.ActivityProfileBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.feed.FeedFragment
import com.example.ppjoke.ui.feed.FeedFragment.Companion.BEHAVIOR_FAVORITE
import com.example.ppjoke.ui.feed.FeedFragment.Companion.TYPE_COLLECTION
import com.example.ppjoke.ui.feed.FeedFragment.Companion.TYPE_PROFILE_FEED
import com.example.ppjoke.ui.my.MyViewModel
import com.example.ppjoke.utils.MMKVUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.xtc.base.BaseMvvmActivity

class ProfileActivity:BaseMvvmActivity<ActivityProfileBinding,MyViewModel>() {
    private val titles = mutableListOf<String>("帖子","收藏")
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private var userId:Long?=null
    private var isMy:Boolean?=null
    private var currentItem=0
    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel=mViewModel
        binding.lifecycleOwner=this
        userId=intent.getLongExtra("USERID",0)
        isMy=intent.getBooleanExtra("ISMY",false)
        currentItem=intent.getIntExtra("CURRENTITEM",0)
        binding.actionBack.setOnClickListener {
            finish()
        }
        fragmentList.add(FeedFragment.newInstance(profileType = "tab_feed",userId=userId, type = TYPE_PROFILE_FEED))
        fragmentList.add(FeedFragment.newInstance(behavior = BEHAVIOR_FAVORITE,userId=userId, type = TYPE_COLLECTION))
        if(isMy==true){
            titles.add(1,"评论")
            titles.add(3,"历史")
            fragmentList.add(1,FeedFragment.newInstance(profileType = "tab_comment",userId=userId, type = TYPE_PROFILE_FEED))
            fragmentList.add(3,FeedFragment.newInstance(behavior = FeedFragment.BEHAVIOR_HISTORY,userId=userId, type = FeedFragment.TYPE_HISTORY))
        }
        binding.viewPagerProfile.adapter=object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return  fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        binding.viewPagerProfile.offscreenPageLimit=1
        binding.viewPagerProfile.currentItem=currentItem
        val tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout, binding.viewPagerProfile
        ) { tab, position -> tab.text = titles[position] }
        tabLayoutMediator.attach()
        binding.btnFollow.visibility=if(userId==MMKVUtils.getInstance().getUserId()) View.GONE else View.VISIBLE
        binding.btnFollow.setOnClickListener {
            val isFollow= InteractionPresenter.toggleFollowUser(userId!!,this)
            mViewModel?.userRelation?.value =isFollow
            val intent = Intent().apply {
                putExtra("KEY_FOLLOW",isFollow)

            }
            setResult(RESULT_OK,intent)
        }
        mViewModel?.getUserInfo(userId!!)
        mViewModel?.getUserRelation(userId!!)

    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.userBean.observe(this){

        }
    }


    override fun getViewBinding(): ActivityProfileBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_profile)
    }

    override fun getViewModel(): MyViewModel {
        return ViewModelProvider(this).get(MyViewModel::class.java)
    }
}