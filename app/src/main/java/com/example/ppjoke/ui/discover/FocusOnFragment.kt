package com.example.ppjoke.ui.discover

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.TagListAdapter
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.bean.UgcBean
import com.example.ppjoke.databinding.FragmentFocusOnBinding
import com.example.ppjoke.databinding.FragmentRecommendBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.login.LoginActivity
import com.jeremyliao.liveeventbus.LiveEventBus
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
        LiveEventBus.get<Map<Int,Any>>("refreshFocusOn").observe(this) {
            refreshItem(it[0] as TagBean, it[1] as Boolean)
        }
        binding!!.refreshLayout.apply {
            setRefreshHeader(BezierRadarHeader(context))
            setRefreshFooter(BallPulseFooter(context))
            setOnRefreshListener {
                refresh()
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

    fun refresh(){
        mViewModel?.getTagList(tagType = "onlyFollow")
    }

    private fun refreshItem(tagBean: TagBean,isAdd:Boolean){
        if (isAdd) {
            adapter!!.addData(tagBean)
        }else{
            adapter!!.remove(tagBean)
        }
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
                        if(InteractionPresenter.checkIsLogin(requireContext(), mViewModel!!.userId)) {
                            mViewModel!!.toggleTagFollow(adapter!!.data[position].tagId!!)
                            LiveEventBus.get<Map<Int, Any>>("refreshRecommend").post(
                                mapOf<Int, Any>(
                                    0 to adapter!!.data[position].tagId!!,
                                    1 to false
                                )
                            )
                            adapter!!.data.removeAt(position)
                            adapter!!.notifyItemRemoved(position)
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
               if(backHasFollow!=true){
                   LiveEventBus.get<Map<Int,Any>>("refreshRecommend")
                       .post(mapOf(0 to adapter!!.data[backPosition].tagId!!,1 to false))
                    adapter!!.removeAt(backPosition)
                    adapter!!.notifyItemRemoved(backPosition)

                }
            }
        }
    }


}