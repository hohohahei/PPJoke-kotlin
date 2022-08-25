package com.example.ppjoke.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide

import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ppjoke.utils.ViewHelper
import com.google.android.material.imageview.ShapeableImageView
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class PPImageView : ShapeableImageView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, 0)
    }

    fun setImageUrl(imageUrl: String?) {
        setImageUrl(this, imageUrl,null)
    }

    fun setPlaceholder(placeUrl:Drawable?){
        setImageUrl(this,null,placeUrl)
    }

    fun bindData(widthPx: Int, heightPx: Int, marginLeft: Int, imageUrl: String?) {
        bindData(
            widthPx,
            heightPx,
            marginLeft,
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenWidth(),
            imageUrl
        )
    }

    fun bindData(
        widthPx: Int,
        heightPx: Int,
        marginLeft: Int,
        maxWidth: Int,
        maxHeight: Int,
        imageUrl: String?
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            visibility = GONE
            return
        } else {
            visibility = VISIBLE
        }
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    val height = resource.intrinsicHeight
                    val width = resource.intrinsicWidth
                    setSize(width, height, marginLeft, maxWidth, maxHeight)
                    setImageDrawable(resource)
                }
            })
            return
        }else {
            setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight)
            setImageUrl(this, imageUrl, null)
        }
    }

    private fun setSize(width: Int, height: Int, marginLeft: Int, maxWidth: Int, maxHeight: Int) {
        val finalWidth: Int
        val finalHeight: Int
        if (width > height) {
            finalWidth = maxWidth
            finalHeight = (height / (width * 1.0f / finalWidth)).toInt()
        } else {
            finalHeight = maxHeight
            finalWidth = (width / (height * 1.0f / finalHeight)).toInt()
        }
        val params = layoutParams
        params.width = finalWidth
        params.height = finalHeight
        if (params is FrameLayout.LayoutParams) {
            params.leftMargin = if (height > width) ConvertUtils.dp2px(marginLeft.toFloat()) else 0
        } else if (params is LinearLayout.LayoutParams) {
            params.leftMargin = if (height > width) ConvertUtils.dp2px(marginLeft.toFloat()) else 0
        }
        layoutParams = params
    }

    companion object {
        @BindingAdapter(value = ["image_url","placeholder"])
        @JvmStatic
        fun setImageUrl(view: PPImageView, imageUrl: String?, placeholder: Drawable?) {
            setImageUrl(view, imageUrl, 0,placeholder)
        }

        @BindingAdapter(value = ["image_url",  "radius","placeholder"], requireAll = false)
        @JvmStatic
        fun setImageUrl(view: PPImageView, imageUrl: String?,  radius: Int,placeholder: Drawable?) {
            val builder = Glide.with(view).load(imageUrl)
            if (placeholder!=null){
                builder.placeholder(placeholder)
                builder.error(placeholder)
            }

             else if (radius > 0) {
                builder.transform(RoundedCornersTransformation(ConvertUtils.dp2px(radius.toFloat()), 0))
            }

            val layoutParams = view.layoutParams
            if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.into(view)
        }


        @BindingAdapter(value = ["blur_url", "radius"])
        @JvmStatic
        fun setBlurImageUrl(imageView: ImageView, blurUrl: String?, radius: Int) {
            Glide.with(imageView).load(blurUrl).override(radius)
                .transform(BlurTransformation())
                .dontAnimate()
                .into(object : SimpleTarget<Drawable?>() {

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        imageView.background = resource
                    }
                })
        }
    }
}