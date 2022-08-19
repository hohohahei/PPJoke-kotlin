package com.example.ppjoke.ui.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.AppGlobals
import com.example.ppjoke.bean.UserBean
import com.example.ppjoke.utils.MMKVUtils

class UserManager private constructor() {
    private val userLiveData: MutableLiveData<UserBean?> = MutableLiveData<UserBean?>()
    private var mUser: UserBean? = null
    fun save(user: UserBean?) {
        if (user != null) {
            mUser = user
//            CacheManager.save(KEY_CACHE_USER, user)
            if (userLiveData.hasObservers()) {
                userLiveData.postValue(user)
            }
        } else {
            if (userLiveData.hasObservers()) {
                userLiveData.postValue(null)
            }
        }
    }

    fun getUserLiveData(): MutableLiveData<UserBean?> {
        return userLiveData
    }

    fun login(context: Context): LiveData<UserBean?> {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return userLiveData
    }

    val isLogin: Boolean
        get() = mUser != null && (mUser!!.expires_time!! > System.currentTimeMillis())
    val user: UserBean?
        get() = if (isLogin) mUser else null
    val userId: Long
        get() = if (isLogin) mUser?.userId!! else 0

    fun refresh(): LiveData<UserBean?> {
        if (!isLogin) {
            return login(AppGlobals.getApplication())
        }
        val liveData: MutableLiveData<UserBean?> = MutableLiveData<UserBean?>()
        //查询用户

        return liveData
    }

    fun logout() {
//        CacheManager.delete(KEY_CACHE_USER, mUser)
        MMKVUtils.getInstance().clearAll()
        userLiveData.value = null
        mUser = null
    }

    companion object {
        private const val KEY_CACHE_USER = "cache_user"
        private val mUserManager = UserManager()

        //必须 单例模式
        fun get(): UserManager {
            return mUserManager
        }
    }

    init {
//        val cache: UserBean = CacheManager.getCache(KEY_CACHE_USER) as UserBean
//        if (cache != null && (cache.expires_time!! > System.currentTimeMillis())) {
//            mUser = cache
//        }
    }
}