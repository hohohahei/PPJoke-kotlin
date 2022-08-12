package com.example.ppjoke.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.example.ppjoke.R

class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    style: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr, style) {
    private val mIvIcon: ImageView
    private val mTvTitle: TextView
    private val mBtAction: Button
    fun setEmptyIcon(@DrawableRes iconRes: Int) {
        mIvIcon.setImageResource(iconRes)
    }

    fun setTitle(text: String?) {
        if (TextUtils.isEmpty(text)) {
            mTvTitle.visibility = GONE
        } else {
            mTvTitle.text = text
            mTvTitle.visibility = VISIBLE
        }
    }

    fun initButton(text: String?, listener: OnClickListener?) {
        if (TextUtils.isEmpty(text)) {
            mBtAction.visibility = GONE
        } else {
            mBtAction.text = text
            mBtAction.visibility = VISIBLE
            mBtAction.setOnClickListener(listener)
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["emptyViewTitle", "emptyViewButtonTitle", "emptyViewButtonListener"])
        fun setEmptyViewTitle(
            view: EmptyView,
            text: String?,
            title: String?,
            listener: OnClickListener?
        ) {
            view.setTitle(text)
            view.initButton(title, listener)
        }
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, true)
        mIvIcon = findViewById(R.id.empty_icon)
        mTvTitle = findViewById(R.id.empty_text)
        mBtAction = findViewById(R.id.empty_action)
    }
}