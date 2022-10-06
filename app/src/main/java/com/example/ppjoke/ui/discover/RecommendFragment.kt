package com.example.ppjoke.ui.discover

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.example.ppjoke.R
import com.example.ppjoke.adapter.TagListAdapter
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.FragmentRecommendBinding
import com.example.ppjoke.ui.binding_action.InteractionPresenter
import com.example.ppjoke.ui.binding_action.InteractionPresenter.checkIsLogin
import com.example.ppjoke.ui.login.LoginActivity
import com.jeremyliao.liveeventbus.LiveEventBus
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.xtc.base.BaseMvvmFragment

class RecommendFragment : BaseMvvmFragment<FragmentRecommendBinding, DiscoverViewModel>() {
    private var adapter: TagListAdapter? = null
    override fun getViewModel(): DiscoverViewModel {
        return ViewModelProvider(this).get(DiscoverViewModel::class.java)
    }


    override fun initView() {
        binding?.lifecycleOwner = this
        binding?.viewModel = mViewModel
        LiveEventBus.get<Map<Int, Any>>("refreshRecommend").observe(this) {
            refreshItemStatus(it[0] as Int, it[1] as Boolean)
        }
        binding!!.refreshLayout.apply {
            setRefreshHeader(BezierRadarHeader(context))
            setRefreshFooter(BallPulseFooter(context))
            setOnRefreshListener {
                refresh()

            }
            setOnLoadMoreListener {
                if (adapter?.data?.size!! > 0 && adapter?.data?.size!! % 10 == 0) {
                    adapter?.data?.last()?.let { it1 -> mViewModel?.loadMoreTag(it1.tagId!!) }
                }

            }
        }
        mViewModel?.getTagList()
    }

    fun refresh() {
        mViewModel?.getTagList()
    }

    private fun refreshItemStatus(tagId: Int, hasFocus: Boolean) {
        adapter!!.data.forEachIndexed { index, tagBean ->
            if (tagBean.tagId == tagId) {
                tagBean.hasFollow = hasFocus
                adapter!!.notifyItemChanged(index)
                return
            }
        }
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.tagList.observe(viewLifecycleOwner) {
            if(binding!!.refreshLayout.isRefreshing) binding!!.refreshLayout.finishRefresh()
            if (adapter == null) {
                adapter = TagListAdapter(ArrayList())
                adapter!!.addData(it)
                adapter!!.setOnItemClickListener { _, _, position ->
                    val intent = Intent(context, TagDetailActivity::class.java)
                    intent.putExtra("TAGBEAN", adapter!!.data[position])
                    intent.putExtra("KEY_POSITION", position)
                    myActivityLauncher.launch(intent)
                }
                adapter!!.addChildClickViewIds(R.id.action_follow)
                adapter!!.setOnItemChildClickListener { _, _, position ->
                    if (checkIsLogin(requireContext())) {
                        mViewModel!!.toggleTagFollow(adapter!!.data[position].tagId!!)
                        adapter!!.data[position].hasFollow = mViewModel!!.tagHasFollow.value
                        adapter!!.notifyItemChanged(position, "followChange")
                        if (mViewModel!!.tagHasFollow.value == true) {
                            LiveEventBus.get<Map<Int, Any>>("refreshFocusOn")
                                .post(mapOf(0 to adapter!!.data[position], 1 to true))
                        } else {
                            LiveEventBus.get<Map<Int, Any>>("refreshFocusOn")
                                .post(mapOf(0 to adapter!!.data[position], 1 to false))
                        }
                    }
                }
            } else {
                adapter!!.setNewInstance(it.toMutableList())
            }
            binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
            binding!!.recyclerView.adapter = adapter
        }
        mViewModel!!.tagLoadMoreList.observe(this) {
            if (binding!!.refreshLayout.isLoading) binding!!.refreshLayout.finishLoadMore()
            adapter?.addData(it)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_recommend
    }


    private val myActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                //回传的数据
                val backHasFollow = activityResult.data?.getBooleanExtra("KEY_FOLLOW", false)
                val backPosition = activityResult.data?.getIntExtra("KEY_POSITION", -1)
                if (backHasFollow != null && backPosition != null && backPosition >= 0) {
                    if (backHasFollow == true) {
                        LiveEventBus.get<Map<Int, Any>>("refreshFocusOn")
                            .post(mapOf(0 to adapter!!.data[backPosition], 1 to true))

                    } else {
                        LiveEventBus.get<Map<Int, Any>>("refreshFocusOn")
                            .post(mapOf(0 to adapter!!.data[backPosition], 1 to false))
                    }
                    adapter?.data!![backPosition].hasFollow = backHasFollow
                    adapter!!.notifyItemChanged(backPosition, "followChange")

                }
            }
        }

}