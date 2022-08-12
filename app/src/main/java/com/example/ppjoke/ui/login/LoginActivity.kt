package com.example.ppjoke.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.databinding.ActivityLoginBinding
import com.tencent.connect.common.Constants
import com.tencent.tauth.Tencent
import com.xtc.base.BaseMvvmActivity

class LoginActivity : BaseMvvmActivity<ActivityLoginBinding,LoginViewModel>(){

    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel=mViewModel
        binding.lifecycleOwner=this
        binding.actionClose.setOnClickListener {
            finish()
        }
        binding.actionLogin.setOnClickListener {
           mViewModel?.requestLogin(this)
        }

    }

    override fun getViewBinding(): ActivityLoginBinding {
        return DataBindingUtil.setContentView(this,R.layout.activity_login)
    }

    override fun getViewModel(): LoginViewModel? {
        return ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun addObserve() {
        super.addObserve()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mViewModel?.listener)
        }
    }

}