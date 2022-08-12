package com.xtc.base

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.xtc.base.utils.toastShort

abstract class BaseMvvmFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment() {
    var binding: VB? = null

    val mViewModel: VM? by lazy { getViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getLayoutId(),
            container,
            false
        ) as VB

        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onProcess(savedInstanceState)
        addObserve()
    }

    abstract fun getViewModel(): VM?

    abstract fun initView()

    abstract fun getLayoutId(): Int

    private fun onProcess(savedInstanceState: Bundle?) {

    }
    open fun addObserve() {
        mViewModel?.run {
            networkError.observe(viewLifecycleOwner) { error ->
                error?.let {
                    onError(error.errorMessage)
                }
            }
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    showLoadingDialog()
                } else {
                    dismissLoadingDialog()
                }
            }
        }

    }

}