package com.example.ppjoke.ui.profile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.repo.UserRepo
import com.example.ppjoke.ui.my.MyViewModel
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel

class ProfileViewModel: MyViewModel() {
    private val repo by lazy { UserRepo() }

    val expand = MutableLiveData<Boolean>(true)
    val userRelation=MutableLiveData<Boolean>()
    //初始化tablayout
    val activity = MutableLiveData<FragmentActivity>()

    fun getUserRelation(userId: Long){
        isLoading.value=true
        launch {
            val respon=repo.queryUserRelation(MMKVUtils.getInstance().getUserId()?:0,userId)
            userRelation.value=respon.data.hasFollow?:false
        }
    }




}