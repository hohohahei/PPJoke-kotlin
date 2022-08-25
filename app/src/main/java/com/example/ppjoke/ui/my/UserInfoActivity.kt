package com.example.ppjoke.ui.my

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ppjoke.R
import com.example.ppjoke.contract.SelectPhotoContract
import com.example.ppjoke.contract.TakePhotoContract
import com.example.ppjoke.databinding.ActivityUserInfoBinding
import com.example.ppjoke.ui.capture.CaptureActivity
import com.example.ppjoke.utils.MMKVUtils
import com.example.ppjoke.widget.dialog.BottomAlbumDialog
import com.xtc.base.BaseMvvmActivity
import com.xtc.base.utils.toastShort
import com.yalantis.ucrop.UCrop
import java.io.File
import kotlin.jvm.internal.Intrinsics

class UserInfoActivity : BaseMvvmActivity<ActivityUserInfoBinding, MyViewModel>() {
    private var outputFilePath: String? = null
    private val deniedPermission = ArrayList<String>()
    private val resolution = Size(300, 300)

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
                    takePhotoLauncher.launch(null)
                }

            }
            dialog.show()
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