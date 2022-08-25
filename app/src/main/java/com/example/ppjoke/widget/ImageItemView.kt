package com.example.ppjoke.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.ppjoke.R

class ImageItemView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyAttrs: Int = 0
) : ConstraintLayout(context, attributeSet, defStyAttrs) {
    private var tvTitle: TextView
    private var imageView: PPImageView
    private var dividerView: View
    private var imageUrl: String?=null
    private var placeholder:Drawable?=null
    private var title:String?=null


    @SuppressLint("CustomViewStyleable")
    val typedArray: TypedArray =
        context.obtainStyledAttributes(attributeSet, R.styleable.MyImageItemView)

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.layout_user_info_iv_item, this)
        tvTitle = root.findViewById(R.id.tv_title)
        imageView = root.findViewById(R.id.iv_image)
        dividerView = root.findViewById(R.id.divider)
        title = typedArray.getString(R.styleable.MyImageItemView_tv_title)
        tvTitle.text=title
        if (typedArray.getBoolean(R.styleable.MyImageItemView_isShowDivider, true)) {
            dividerView.visibility = VISIBLE
        } else {
            dividerView.visibility = GONE
        }

    }

    companion object{
        @JvmStatic
        @BindingAdapter(value = ["image_url","placeholder"],requireAll = false)
        fun setImageItemView(
            view: ImageItemView,
            imageUrl: String?,placeholder:Drawable?
        ){
            view.setImageUrl(imageUrl)
            view.setPlaceholder(placeholder)
        }

    }

    fun setTitle(title: String?){
        this.title=title
        tvTitle.text=title
    }

    fun setImageUrl(url:String?){
        this.imageUrl=url
        imageView.setImageUrl(url)
    }

    fun setPlaceholder(placeholder: Drawable?){
        this.placeholder=placeholder
        imageView.setPlaceholder(placeholder)
    }
}