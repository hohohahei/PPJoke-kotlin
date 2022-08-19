package com.example.ppjoke.ui.my

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.databinding.FragmentMyBinding
import com.example.ppjoke.ui.login.LoginActivity
import com.example.ppjoke.ui.profile.ProfileActivity
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
        binding?.userFeed?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("USERID",MMKVUtils.getInstance().getUserId())
            intent.putExtra("ISMY",true)
            intent.putExtra("CURRENTITEM",0)
            startActivity(intent)
        }
        binding?.userComment?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("USERID",MMKVUtils.getInstance().getUserId())
            intent.putExtra("ISMY",true)
            intent.putExtra("CURRENTITEM",1)
            startActivity(intent)
        }
        binding?.userFavorite?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("USERID",MMKVUtils.getInstance().getUserId())
            intent.putExtra("ISMY",true)
            intent.putExtra("CURRENTITEM",2)
            startActivity(intent)
        }
        binding?.userHistory?.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("USERID",MMKVUtils.getInstance().getUserId())
            intent.putExtra("ISMY",true)
            intent.putExtra("CURRENTITEM",3)
            startActivity(intent)
        }
        binding?.fansCount?.setOnClickListener {
            val intent = Intent(context, FansAndFollowsActivity::class.java)
            intent.putExtra("TITLE","粉丝列表")
            intent.putExtra("TYPE",0)
            intent.putExtra("COUNT",mViewModel!!.followCount.value)
            myActivityLauncher.launch(intent)
        }
        binding?.followCount?.setOnClickListener {
            val intent = Intent(context, FansAndFollowsActivity::class.java)
            intent.putExtra("TITLE","关注列表")
            intent.putExtra("TYPE",1)
            intent.putExtra("COUNT", mViewModel!!.followCount.value)
            myActivityLauncher.launch(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_my
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val backFollowCount= activityResult.data?.getIntExtra("KEY_FOLLOW_COUNT",0)
            if(backFollowCount!=null){
                mViewModel?.followCount?.value=backFollowCount
            }
        }
    }

}