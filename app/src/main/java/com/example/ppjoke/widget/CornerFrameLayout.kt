package com.example.ppjoke.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.ppjoke.utils.ViewHelper

class CornerFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    fun setViewOutline(radius: Int, radiusSide: Int) {
        ViewHelper.setViewOutline(this, radius, radiusSide)
    }

    init {
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, defStyleRes)
    }
}
