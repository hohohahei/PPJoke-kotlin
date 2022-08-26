package com.example.ppjoke.ui.my

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.example.ppjoke.bean.UserBean
import com.example.ppjoke.repo.UserRepo
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel

open class MyViewModel : BaseViewModel() {
    private val repo by lazy { UserRepo() }
    val userBean=MutableLiveData<UserBean?>()
    val expand = MutableLiveData<Boolean>(true)
    val userRelation=MutableLiveData<Boolean>()
    //初始化tablayout
    val activity = MutableLiveData<FragmentActivity>()
    val fansList= MutableLiveData<List<UserBean>>()
    val followList=MutableLiveData<List<UserBean>>()

    val fansCount=MutableLiveData<Int>()
    val followCount=MutableLiveData<Int>()
    val updateStatus=MutableLiveData<Boolean>()

    fun getUserRelation(userId: Long){
        isLoading.value=true
        launch {
            val respon=repo.queryUserRelation(MMKVUtils.getInstance().getUserId()?:0,userId)
            userRelation.value=respon.data.hasFollow?:false
        }
    }
    fun getUserInfo(userId:Long){
        isLoading.value=true
        launch {
            val response=repo.queryUserInfo(userId)
            userBean.value= response.data
            fansCount.value=response.data.followerCount?:0
            followCount.value=response.data.followCount?:0
            isLoading.value=false
        }
    }

    fun queryFans(userId: Long){
        isLoading.value=true
        launch {
            val response=repo.queryFans(userId)
            fansList.value=response.data
            isLoading.value=false
        }
    }

    fun queryFollows(userId: Long){
        isLoading.value=true
        launch {
            val response=repo.queryFollows(userId)
            followList.value=response.data
            isLoading.value=false
        }
    }

    fun saveUserInfo(){
        println("上传的user：${userBean.value?.description}")
        launch {
            var user=GsonUtils.toJson(userBean.value)
            val response=  repo.updateUser(user)
            updateStatus.value=true
        }
    }
}