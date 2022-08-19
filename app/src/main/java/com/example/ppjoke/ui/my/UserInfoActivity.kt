package com.example.ppjoke.ui.my

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.databinding.ActivityUserInfoBinding
import com.xtc.base.BaseMvvmActivity

class UserInfoActivity:BaseMvvmActivity<ActivityUserInfoBinding,MyViewModel>() {
    override fun initView(savedInstanceState: Bundle?) {
        setToolBarTitle("用户信息")
        binding.lifecycleOwner=this
    }

    override fun getViewBinding(): ActivityUserInfoBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_user_info)
    }

    override fun getViewModel(): MyViewModel {
        return ViewModelProvider(this).get(MyViewModel::class.java)
    }
}