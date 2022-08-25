package com.example.ppjoke.widget.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import com.example.ppjoke.R
import com.example.ppjoke.databinding.LayoutBottomAlbumDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomAlbumDialog(context: Context):BottomSheetDialog(context) {
    private lateinit var binding:LayoutBottomAlbumDialogBinding
    var albumDialogClick: AlbumDialogClick? = null
    interface AlbumDialogClick{
        fun openAlbum()
        fun takePicture()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.layout_bottom_album_dialog,null,false)

        binding.tvAlbum.setOnClickListener {
              albumDialogClick?.openAlbum()
        }
        binding.tvPhotograph.setOnClickListener {  }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        setContentView(binding.root)
    }
}