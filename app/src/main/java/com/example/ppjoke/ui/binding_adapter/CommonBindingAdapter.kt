package com.example.ppjoke.ui.binding_adapter

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.example.ppjoke.bean.CouchTabBean
import com.example.ppjoke.bean.TabBean
import com.example.ppjoke.ui.home.HomeFragment
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

object CommonBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["imageUrl"], requireAll = false)
    fun setImageUrl(photoView: PhotoView, imageUrl: String?) {
        Glide.with(photoView.context).load(imageUrl).into(photoView)
    }

    @JvmStatic
    @BindingAdapter(value = ["player"], requireAll = false)
    fun setPlayer(playerview: PlayerView, player: SimpleExoPlayer?) {
        playerview.player = player
    }

    @JvmStatic
    @BindingAdapter(value = ["offsetChangeListener"], requireAll = false)
    fun setOffsetChangeListener(
        appBarLayout: AppBarLayout,
        offsetChangeListener: OnOffsetChangedListener?
    ) {
        appBarLayout.addOnOffsetChangedListener(offsetChangeListener)
    }

    @JvmStatic
    @BindingAdapter(value = ["onRefreshListener"], requireAll = false)
    fun setOffsetChangeListener(refreshLayout: SmartRefreshLayout, listener: OnRefreshListener?) {
        refreshLayout.setOnRefreshListener(listener)
    }

    @JvmStatic
    @BindingAdapter(value = ["onLoadMoreListener"], requireAll = false)
    fun setOnLoadMoreListener(refreshLayout: SmartRefreshLayout, listener: OnLoadMoreListener?) {
        refreshLayout.setOnLoadMoreListener(listener)
    }

    @JvmStatic
    @BindingAdapter(value = ["currentItem"], requireAll = false)
    fun bindCurrentItem(viewPager2: ViewPager2, currentItem: Int) {
        viewPager2.setCurrentItem(currentItem, true)
    }

    @JvmStatic
    @BindingAdapter(value = ["onClickWithDebouncing"], requireAll = false)
    fun onClickWithDebouncing(view: View?, clickListener: View.OnClickListener?) {
        ClickUtils.applySingleDebouncing(view, clickListener)
    }

    @JvmStatic
    @BindingAdapter(value = ["showSoftInputMethod"])
    fun showSoftInputMethod(editText: AppCompatEditText, show: Boolean) {
        if (show) {
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            //请求获得焦点
            editText.requestFocus()
            val manager = Utils.getApp().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(editText, 0)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["alpha"], requireAll = false)
    fun setImageRes(imageView: ImageView, alpha: Int) {
        imageView.setAlpha(alpha)
    }

    @JvmStatic
    @BindingAdapter(value = ["enabled"], requireAll = false)
    fun setImageRes(imageView: ImageView, enabled: Boolean) {
        imageView.isEnabled = enabled
    }

    @JvmStatic
    @BindingAdapter(value = ["removedItemDecoration"], requireAll = false)
    fun removeItemDecorationAt(recyclerView: RecyclerView, item: Int) {
        recyclerView.removeItemDecorationAt(item)
    }

    @JvmStatic
    @BindingAdapter(value = ["scrollToPosition"], requireAll = false)
    fun scrollToPosition(recyclerView: RecyclerView, position: Int) {
        recyclerView.scrollToPosition(position)
    }

}