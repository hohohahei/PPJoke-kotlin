package com.example.ppjoke.ui.discover

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.TagListAdapter
import com.example.ppjoke.bean.UgcBean
import com.example.ppjoke.databinding.FragmentFocusOnBinding
import com.example.ppjoke.databinding.FragmentRecommendBinding
import com.example.ppjoke.ui.login.LoginActivity
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.xtc.base.BaseMvvmFragment


class FocusOnFragment : BaseMvvmFragment<FragmentRecommendBinding,DiscoverViewModel>(){
    private var adapter: TagListAdapter?=null
    override fun getViewModel(): DiscoverViewModel {
        return ViewModelProvider(this).get(DiscoverViewModel::class.java)
    }

    override fun initView() {
        binding?.lifecycleOwner=this
        binding?.viewModel=mViewModel
        binding!!.refreshLayout.apply {
            setRefreshHeader(BezierRadarHeader(context))
            setRefreshFooter(BallPulseFooter(context))
            setOnRefreshListener {
                mViewModel?.getTagList(tagType = "onlyFollow")
                finishRefresh(2000)
            }
            setOnLoadMoreListener {
                if(adapter?.data?.size!! >0&&adapter?.data?.size!!%10==0) {
                    adapter?.data?.last()?.let { it1 -> mViewModel?.loadMoreTag(it1.tagId!!, "onlyFollow") }
                }
                finishLoadMore(2000)

            }
        }
        mViewModel?.getTagList(tagType = "onlyFollow")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_recommend
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.tagList.observe(viewLifecycleOwner){
            if(adapter==null){
                adapter= TagListAdapter(ArrayList())
                adapter!!.addData(it)
                adapter!!.setOnItemClickListener { _, _, position ->
                    val intent= Intent(context,TagDetailActivity::class.java)
                    intent.putExtra("TAGBEAN", adapter!!.data[position])
                    intent.putExtra("KEY_POSITION",position)
                    myActivityLauncher.launch(intent)
                }
                adapter!!.addChildClickViewIds(R.id.action_follow)
                adapter!!.setOnItemChildClickListener { _, view, position ->
                    if (mViewModel!!.userId!=null) {
                        mViewModel!!.toggleTagFollow(adapter!!.data[position].tagId!!)
                        adapter!!.data.removeAt(position)
                        adapter!!.notifyItemRemoved(position)
                    }else{
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }else{
                adapter!!.setNewInstance(it.toMutableList())
            }
            binding!!.recyclerView.layoutManager= LinearLayoutManager(context)
            binding!!.recyclerView.adapter=adapter
        }
        mViewModel!!.tagLoadMoreList.observe(this){
            adapter?.addData(it)
        }
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            //回传的数据
            val backHasFollow= activityResult.data?.getBooleanExtra("KEY_FOLLOW",false)
            val backPosition=activityResult.data?.getIntExtra("KEY_POSITION",-1)
            if (backHasFollow != null&&backPosition!=null&&backPosition>=0) {
                if(!backHasFollow){
                    adapter!!.removeAt(backPosition)
                    adapter!!.notifyItemRemoved(backPosition)
                }
            }
        }
    }


}