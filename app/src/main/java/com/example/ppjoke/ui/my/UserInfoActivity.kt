package com.example.ppjoke.ui.my

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.text.TextUtils
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.blankj.utilcode.util.ThreadUtils
import com.example.ppjoke.R
import com.example.ppjoke.contract.SelectPhotoContract
import com.example.ppjoke.contract.TakePhotoContract
import com.example.ppjoke.databinding.ActivityUserInfoBinding
import com.example.ppjoke.ui.capture.CaptureActivity
import com.example.ppjoke.ui.publish.UploadFileWork
import com.example.ppjoke.utils.FileUtils
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.BottomAlbumDialog
import com.example.ppjoke.widget.dialog.LoadingDialog
import com.xtc.base.BaseMvvmActivity
import com.xtc.base.utils.toastShort
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.internal.Intrinsics

class UserInfoActivity : BaseMvvmActivity<ActivityUserInfoBinding, MyViewModel>() {
    private var outputFilePath: String? = null
    private val deniedPermission = ArrayList<String>()
    private val resolution = Size(300, 300)
    private var mLoadingDialog: LoadingDialog? = null
    private var coverUploadUUID: UUID? = null
    private  var fileUploadUUID: UUID? = null
    private var coverUploadUrl: String? = null
    private  var fileUploadUrl:String? = null

    companion object {
        const val PERMISSION_ALBUM_CODE = 1000
        const val PERMISSION_TAKE_PHOTO_CODE = 1001
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        setToolBarTitle("用户信息")
        binding.lifecycleOwner = this
        MMKVUtils.getInstance().getUserId()?.let { mViewModel?.getUserInfo(it) }
        binding.btnSave.setOnClickListener {
            toastShort("个性签名 ${binding.userBean?.description}")
            toastShort("昵称 ${binding.userBean?.name}")
        }
        binding.userAvtar.setOnClickListener {
            var dialog = BottomAlbumDialog(this)
            dialog.albumDialogClick = object : BottomAlbumDialog.AlbumDialogClick {
                override fun openAlbum() {
                    ActivityCompat.requestPermissions(
                        this@UserInfoActivity,
                        PERMISSIONS,
                        PERMISSION_ALBUM_CODE
                    )


                }

                override fun takePicture() {
                    ActivityCompat.requestPermissions(
                        this@UserInfoActivity,
                        PERMISSIONS,
                        PERMISSION_TAKE_PHOTO_CODE
                    )

                }

            }
            dialog.show()
        }
        binding.btnSave.setOnClickListener {
            save()
        }
    }

    override fun getViewBinding(): ActivityUserInfoBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_user_info)
    }

    override fun getViewModel(): MyViewModel {
        return ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel?.userBean?.observe(this) {
            binding.userBean = mViewModel!!.userBean.value
        }
    }

    private fun takePicture(code: Int) {
        if (code == PERMISSION_ALBUM_CODE) {
            selectPhotoLauncher.launch(null)
        } else {
            takePhotoLauncher.launch(null)
        }
    }

    /**
     * 打开相机拍照，并前往裁剪
     */
    private val takePhotoLauncher = (this as ComponentActivity).registerForActivityResult(
        TakePhotoContract()
    ) { uri ->
        uri?.let {
            doCrop(it)
        }
    }

    /**
     * 前往相册选择照片，并前往裁剪
     */
    private val selectPhotoLauncher = (this as ComponentActivity).registerForActivityResult(
        SelectPhotoContract()
    ) { uri ->
        uri?.let {
            doCrop(it)
        }
    }


    private fun doCrop(sourceUri: Uri) {
        Intrinsics.checkParameterIsNotNull(sourceUri, "资源为空")
        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)
            setShowCropGrid(false)
        }
        UCrop.of(sourceUri, getDestinationUri())
            .withMaxResultSize(300, 300)
            .withAspectRatio(1F, 1F)
            .withOptions(options)
            .start(this)
    }

    private fun getDestinationUri(): Uri {
        val cropFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            System.currentTimeMillis().toString() + ".jpg"
        )
        println("图片地址：${cropFile.path}")
        outputFilePath = cropFile.absolutePath
        return Uri.fromFile(cropFile)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALBUM_CODE || requestCode == PERMISSION_TAKE_PHOTO_CODE) {
            deniedPermission.clear()
            for (i in permissions.indices) {
                val permission = permissions[i]
                val result = grantResults[i]
                if (result != PackageManager.PERMISSION_GRANTED) {
                    if (permission != null) {
                        deniedPermission.add(permission)
                    }
                }
            }
            if (deniedPermission.isEmpty()) {
                takePicture(requestCode)
            }
        } else {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.capture_permission_message))
                .setNegativeButton(
                    getString(R.string.capture_permission_no)
                ) { dialog, which ->
                    dialog.dismiss()
                    finish()
                }
                .setPositiveButton(
                    getString(R.string.capture_permission_ok)
                ) { _, _ ->
                    val denied = arrayOfNulls<String>(deniedPermission.size)
                    ActivityCompat.requestPermissions(
                        this,
                        deniedPermission.toArray<String>(denied),
                        CaptureActivity.PERMISSION_CODE
                    )
                }.create().show()
        }
    }

    private fun enqueue(workRequests: List<OneTimeWorkRequest>) {
        val workContinuation = WorkManager.getInstance(this).beginWith(workRequests)
        workContinuation.enqueue()
        workContinuation.workInfosLiveData.observe(
            this
        ) { workInfos -> //block runing enuqued failed susscess finish
            var completedCount = 0
            var failedCount = 0
            for (workInfo in workInfos) {
                val state = workInfo.state
                val outputData = workInfo.outputData
                val uuid = workInfo.id
                if (state == WorkInfo.State.FAILED) {
                    // if (uuid==coverUploadUUID)是错的
                    if (uuid == coverUploadUUID) {
                        toastShort(getString(R.string.file_upload_cover_message))
                    } else if (uuid == fileUploadUUID) {
                        toastShort(getString(R.string.file_upload_original_message))
                    }
                    failedCount++
                } else if (state == WorkInfo.State.SUCCEEDED) {
                    val fileUrl = outputData.getString("fileUrl")
                    if (uuid == coverUploadUUID) {
                        coverUploadUrl = fileUrl
                    } else if (uuid == fileUploadUUID) {
                        fileUploadUrl = fileUrl
                    }
                    completedCount++
                }
            }
            if (completedCount >= workInfos.size) {
                mViewModel!!.userBean.value?.avatar=fileUploadUrl
                mViewModel!!.saveUserInfo()
            } else if (failedCount > 0) {
                dismissLoading()
            }
        }
    }

    private fun showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this)
            mLoadingDialog!!.setLoadingText(getString(R.string.feed_publish_ing))
        }
        mLoadingDialog!!.show()
    }

    private fun dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog!!.dismiss()
            }
        } else {
            ThreadUtils.runOnUiThread {
                if (mLoadingDialog != null) {
                    mLoadingDialog!!.dismiss()
                }
            }
        }
    }

    fun save(){
        showLoading()
        val workRequests: MutableList<OneTimeWorkRequest> = java.util.ArrayList()
        if (!TextUtils.isEmpty(outputFilePath)) {

            val request = getOneTimeWorkRequest(outputFilePath!!)
            fileUploadUUID = request.id
            workRequests.add(request)
            //如果是视频文件则需要等待封面文件生成完毕后再一同提交到任务队列
            //否则 可以直接提交了
            enqueue(workRequests)
        } else {
            println("返回图片地址2：$fileUploadUrl")
            mViewModel!!.saveUserInfo()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun getOneTimeWorkRequest(filePath: String): OneTimeWorkRequest {
        val inputData: Data = Data.Builder()
            .putString("file", filePath)
            .build()
        return OneTimeWorkRequest.Builder(UploadFileWork::class.java)
            .setInputData(inputData) //                .setConstraints(constraints)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                      binding.userAvtar.setImageUrl(outputFilePath)
//                    val intent = Intent()
//                    intent.putExtra(CaptureActivity.RESULT_FILE_PATH, outputFilePath)
//                    //当设备处于竖屏情况时，宽高的值 需要互换，横屏不需要
//                    intent.putExtra(CaptureActivity.RESULT_FILE_WIDTH, resolution.height)
//                    intent.putExtra(CaptureActivity.RESULT_FILE_HEIGHT, resolution.width)
//                    intent.putExtra(CaptureActivity.RESULT_FILE_TYPE, true)
//                    setResult(RESULT_OK, intent)
//                    finish()
                }
            }
        }

    }

}