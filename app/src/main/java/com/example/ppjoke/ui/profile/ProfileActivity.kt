package com.example.ppjoke.ui.profile

import android.os.Bundle
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
import com.google.android.material.tabs.TabLayoutMediator
import com.xtc.base.BaseMvvmActivity

class ProfileActivity:BaseMvvmActivity<ActivityProfileBinding,ProfileViewModel>() {
    private val titles = arrayOf("帖子","收藏")
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private var userId:Long?=null
    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel=mViewModel
        binding.lifecycleOwner=this
        userId=intent.getLongExtra("USERID",0)
        binding.actionBack.setOnClickListener {
            finish()
        }
        fragmentList.add(FeedFragment.newInstance(profileType = "tab_feed",userId=userId, type = TYPE_PROFILE_FEED))
        fragmentList.add(FeedFragment.newInstance(behavior = BEHAVIOR_FAVORITE,userId=userId, type = TYPE_COLLECTION))
        binding.viewPagerProfile.adapter=object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return  fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        binding.viewPagerProfile.offscreenPageLimit=2
        val tabLayoutMediator = TabLayoutMediator(
            binding.tabLayout, binding.viewPagerProfile
        ) { tab, position -> tab.text = titles[position] }
        tabLayoutMediator.attach()
        binding.btnFollow.setOnClickListener {
            val isFollow= InteractionPresenter.toggleFollowUser(userId!!)
            mViewModel?.userRelation?.value =isFollow
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

    override fun getViewModel(): ProfileViewModel {
        return ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
}