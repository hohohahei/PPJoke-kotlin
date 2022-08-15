package com.example.ppjoke.widget.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ppjoke.R
import com.example.ppjoke.adapter.TagsAdapter
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.LayoutBottomTagsDialogBinding
import com.example.ppjoke.ui.discover.DiscoverViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomTagDialog: BottomSheetDialogFragment() {
    private lateinit var binding:LayoutBottomTagsDialogBinding
    private  var tagsAdapter:TagsAdapter?=null
    lateinit var viewModel: DiscoverViewModel
    private val mTagLists: MutableList<TagBean> = ArrayList<TagBean>()
    private var listener: OnTagItemSelectedListener? = null

    fun setOnTagItemSelectedListener(listener: OnTagItemSelectedListener) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog= super.onCreateDialog(savedInstanceState)
        binding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.layout_bottom_tags_dialog,null,false)
        viewModel=ViewModelProvider(this).get(DiscoverViewModel::class.java)
        queryTagList()
        dialog.setContentView(binding.root)
        return dialog
    }

    private fun queryTagList(){
        viewModel.run {
            getTagList(pageCount = 20)
            tagList.observe(this@BottomTagDialog){
                if(tagsAdapter==null){
                    tagsAdapter= TagsAdapter(ArrayList())
                    tagsAdapter!!.addData(it)
                    tagsAdapter!!.setOnItemClickListener{ _, _, position->
                        if(listener!=null){
                            listener!!.onTagItemSelected(tagsAdapter!!.data[position])
                            dismiss()
                        }
                    }

                }else{
                    tagsAdapter!!.setNewInstance(it.toMutableList())
                }
                binding.recyclerView.layoutManager=GridLayoutManager(context,4)
                binding.recyclerView.adapter=tagsAdapter

            }
        }

    }
    interface OnTagItemSelectedListener {
        fun onTagItemSelected(tagBean: TagBean)
    }
}