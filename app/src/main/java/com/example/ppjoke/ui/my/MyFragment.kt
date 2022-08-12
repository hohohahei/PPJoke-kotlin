package com.example.ppjoke.ui.my

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.get
import com.example.ppjoke.R
import com.example.ppjoke.databinding.FragmentMyBinding
import com.example.ppjoke.ui.feed.FeedFragment
import com.example.ppjoke.ui.feed.FeedFragment.Companion.TYPE_TAG
import com.example.ppjoke.ui.login.LoginActivity
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseMvvmFragment

class MyFragment : BaseMvvmFragment<FragmentMyBinding,MyViewModel>() {
     private var userId:Long?=null
    override fun getViewModel(): MyViewModel? {
        return ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun initView() {
        binding?.lifecycleOwner=this
        binding?.viewModel=mViewModel
        userId=MMKVUtils.getInstance().getUserId()
        if (userId!=null){
            mViewModel?.getUserInfo(userId!!)
        }
        binding?.layoutUserInfo?.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

}