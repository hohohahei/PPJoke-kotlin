package com.example.ppjoke.widget.dialog

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R

import com.example.ppjoke.databinding.LayoutCommentDialogBinding
import com.example.ppjoke.ui.capture.CaptureActivity
import com.example.ppjoke.ui.detail.FeedDetailViewModel
import com.xtc.base.utils.toastShort
import com.yalantis.ucrop.UCrop
import java.io.File
import kotlin.jvm.internal.Intrinsics

class CommentDialog: DialogFragment() {
    lateinit var binding: LayoutCommentDialogBinding
    lateinit var mViewModel: FeedDetailViewModel
    private var width = 0
    private  var height:Int = 0
    private var imagePath:String?=null
    var itemId:Long?=null


    companion object{
        const val REQ_CAPTURE = 10001 //相机
        const val REQ_ALBUM=10002  //相册
        private const val TAG="CommentDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_comment_dialog,null,false)
        mViewModel=ViewModelProvider(this).get(FeedDetailViewModel::class.java)
        binding.lifecycleOwner=this
        binding.viewModel=mViewModel
        binding.commentSend.setOnClickListener {
            val contentText=binding.inputView.text.toString()
            if(contentText.isNotEmpty()) {
                mViewModel.addComment(itemId!!, contentText)
            }
        }
        binding.commentPicture.setOnClickListener{
             openAlbum()
        }
        binding.commentVideo.setOnClickListener{
            val intent=Intent(activity,CaptureActivity::class.java)
            startActivityForResult(intent, REQ_CAPTURE)
        }
        binding.commentDelete.setOnClickListener {
            width = 0
            height = 0
            mViewModel.isVideo.value=false
            mViewModel.filePath.value=null
            mViewModel.commentVideoImageAlpha.value=255
        }
        val dialog=Dialog(requireActivity(),0)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)
        val window:Window?=dialog.window
        window?.run {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp:WindowManager.LayoutParams=attributes
            wlp.width =  WindowManager.LayoutParams.MATCH_PARENT
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
            wlp.gravity=Gravity.BOTTOM
            window.attributes=wlp
        }
        return dialog
    }

    private fun openAlbum(){
        val intent=Intent()
        intent.type="image/*"
        intent.action="android.intent.action.GET_CONTENT"
        intent.addCategory("android.intent.category.OPENABLE")
        startActivityForResult(intent, REQ_ALBUM)
    }

    private fun doCrop(sourceUri: Uri){
        Intrinsics.checkParameterIsNotNull(sourceUri,"资源为空")
        UCrop.of(sourceUri,getDestinationUri())
            .withMaxResultSize(500,500)
            .start(requireContext(),this,UCrop.REQUEST_CROP)
    }

    private fun getDestinationUri(): Uri {
        val cropFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            System.currentTimeMillis().toString() + ".jpg")
        println("图片地址：${cropFile.path}")
        imagePath=cropFile.path
        return Uri.fromFile(cropFile)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG,"返回的值：$requestCode")
        if(resultCode==RESULT_OK){
            when(requestCode){
                REQ_ALBUM->{
                   doCrop(data?.data!!)
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri: Uri = UCrop.getOutput(data!!)!!
                    val bitmap =
                        BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(resultUri))
                    mViewModel.filePath.value=imagePath
                }
                REQ_CAPTURE->{
                    val filePath = data!!.getStringExtra(CaptureActivity.RESULT_FILE_PATH)
                    width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0)
                    height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0)
                    val isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false)

                    mViewModel.filePath.value=filePath
                    mViewModel.isVideo.value=isVideo
                    mViewModel.commentVideoImageAlpha.value=80
                }
            }
        }
    }
}