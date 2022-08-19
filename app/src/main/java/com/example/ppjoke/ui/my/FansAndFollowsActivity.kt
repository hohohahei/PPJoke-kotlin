package com.example.ppjoke.ui.my

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.FansAndFollowsAdapter
import com.example.ppjoke.databinding.ActivityFansFollowsBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.profile.ProfileActivity
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseMvvmActivity

class FansAndFollowsActivity:BaseMvvmActivity<ActivityFansFollowsBinding,MyViewModel>() {
    private var type:Int?=null
    private var userId=MMKVUtils.getInstance().getUserId()
    private var adapter:FansAndFollowsAdapter?=null

    override fun initView(savedInstanceState: Bundle?) {
        setToolBarTitle(intent.getStringExtra("TITLE"))
        type=intent.getIntExtra("TYPE",0)
        binding.lifecycleOwner=this
        if(type==0){
            userId?.let { mViewModel?.queryFans(it) }
        }else{
            userId?.let { mViewModel?.queryFollows(it) }
        }

    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.fansList.observe(this){
            if(adapter==null){
                adapter= FansAndFollowsAdapter(it.toMutableList())
                adapter!!.addChildClickViewIds(R.id.user_avtar)
                adapter!!.addChildClickViewIds(R.id.action_follow)
                adapter!!.setOnItemChildClickListener{_,view,position->
                     when(view.id){
                         R.id.user_avtar->{
                             val intent = Intent(this, ProfileActivity::class.java)
                             intent.putExtra("USERID", adapter!!.data[position].userId)
                             startActivity(intent)
                         }
                         R.id.action_follow->{
                             val isFollow = InteractionPresenter.toggleFollowUser(adapter!!.data[position].userId!!)

                         }
                     }
                }
                binding.recyclerView.layoutManager=LinearLayoutManager(this)
                binding.recyclerView.adapter=adapter

            }else{
                adapter!!.setNewInstance(it.toMutableList())
            }
        }
        mViewModel!!.followList.observe(this){
            if(adapter==null){
                adapter= FansAndFollowsAdapter(it.toMutableList())
                binding.recyclerView.layoutManager=LinearLayoutManager(this)
                binding.recyclerView.adapter=adapter
                adapter!!.setOnItemChildClickListener{_,view,position->
                    when(view.id){
                        R.id.user_avtar->{
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("USERID", adapter!!.data[position].userId)
                            startActivity(intent)
                        }
                        R.id.action_follow->{
                            val isFollow = InteractionPresenter.toggleFollowUser(adapter!!.data[position].userId!!)

                        }
                    }
                }
                binding.recyclerView.layoutManager=LinearLayoutManager(this)
                binding.recyclerView.adapter=adapter
            }else{
                adapter!!.setNewInstance(it.toMutableList())
            }
        }
    }

    override fun getViewBinding(): ActivityFansFollowsBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_fans_follows)
    }

    override fun getViewModel(): MyViewModel? {
        return ViewModelProvider(this).get(MyViewModel::class.java)
    }
}