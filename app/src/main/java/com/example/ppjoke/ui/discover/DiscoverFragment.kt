package com.example.ppjoke.ui.discover

import android.R.id.tabs
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ppjoke.R
import com.example.ppjoke.databinding.FragmentDiscoverBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.xtc.base.BaseMvvmFragment


class DiscoverFragment : BaseMvvmFragment<FragmentDiscoverBinding,DiscoverViewModel>() {
    private val titles = arrayOf("关注", "推荐")
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun initView() {
        binding?.viewModel=mViewModel
        binding?.lifecycleOwner=this
        fragmentList.add(FocusOnFragment())
        fragmentList.add(RecommendFragment())
        binding!!.viewPagerDiscover.adapter=object :FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return  fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        binding!!.viewPagerDiscover.offscreenPageLimit=1
        val tabLayoutMediator = TabLayoutMediator(
            binding!!.tabLayout, binding!!.viewPagerDiscover
        ) { tab, position -> tab.text = titles[position] }
        tabLayoutMediator.attach()

    }

    override fun getViewModel(): DiscoverViewModel {
        return ViewModelProvider(this).get(DiscoverViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_discover
    }
}