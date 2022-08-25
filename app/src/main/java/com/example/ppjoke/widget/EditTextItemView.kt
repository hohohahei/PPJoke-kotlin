package com.example.ppjoke.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.ppjoke.R

class EditTextItemView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyAttrs: Int = 0
) : ConstraintLayout(context, attributeSet, defStyAttrs) {
    private var tvTitle: TextView
    private var editTextView: EditText
    private var dividerView: View
    private var hint: String? = null
    private var content: String? = null

    @SuppressLint("CustomViewStyleable")
    val typedArray: TypedArray =
        context.obtainStyledAttributes(attributeSet, R.styleable.MyEditTextItemView)

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.layout_user_info_ev_item, this)
        tvTitle = root.findViewById(R.id.tv_title)
        editTextView = root.findViewById(R.id.et_content)
        dividerView = root.findViewById(R.id.divider)

        hint = typedArray.getString(R.styleable.MyEditTextItemView_edt_hint)
        editTextView.hint = hint

        tvTitle.text = typedArray.getString(R.styleable.MyEditTextItemView_tv_title)
        editTextView.inputType = typedArray.getInt(
            R.styleable.MyEditTextItemView_android_inputType,
            InputType.TYPE_CLASS_TEXT
        )

        if (typedArray.getBoolean(R.styleable.MyEditTextItemView_isShowDivider, true)) {
            dividerView.visibility = VISIBLE
        } else {
            dividerView.visibility = GONE
        }

    }
    companion object{
        @JvmStatic
        @BindingAdapter("edt_content")
        fun setTextItemView( view: EditTextItemView,
                            content: String?){
            view.setContent(content)
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "edt_content", event = "edt_contentAttrChanged")
        fun getEditContentText(view: EditTextItemView):String{
            return view.editTextView.text.toString()
        }

        @JvmStatic
        @BindingAdapter("edt_contentAttrChanged")
        fun setChangeListener(view: EditTextItemView,listener:InverseBindingListener?){
            var txt=""
            view.editTextView.addTextChangedListener {
                if(txt!=it.toString()){
                    listener?.onChange()
                    txt=it.toString()
                }
                view.editTextView.setSelection(txt.length)
            }
        }


    }

    fun setText(title:String?){
        tvTitle.text=title
    }

    fun setContent(content:String?){
        editTextView.setText(content)
    }

    fun setHint(hint: String?){
        editTextView.hint = hint
    }
}