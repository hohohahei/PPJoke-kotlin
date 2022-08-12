package com.example.ppjoke.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ppjoke.bean.UserBean
import com.example.ppjoke.repo.UserRepo
import com.xtc.base.BaseViewModel

open class MyViewModel : BaseViewModel() {
    private val repo by lazy { UserRepo() }
    val userBean=MutableLiveData<UserBean?>()
    fun getUserInfo(userId:Long){
        isLoading.value=true
        launch {
            val response=repo.queryUserInfo(userId)
            userBean.value= response.data
            isLoading.value=false
        }
    }
}