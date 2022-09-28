package com.example.ppjoke.ui.capture

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ppjoke.R
import com.example.ppjoke.databinding.ActivityCaptureBinding
import com.example.ppjoke.widget.RecordView
import com.example.ppjoke.widget.dialog.CommentDialog.Companion.REQ_ALBUM
import com.xtc.base.BaseMvvmActivity
import com.xtc.base.utils.toastShort
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.jvm.internal.Intrinsics
import kotlin.math.roundToInt


class CaptureActivity : BaseMvvmActivity<ActivityCaptureBinding, CaptureViewModel>() {
    private var imageCapture: ImageCapture? = null
    private lateinit var videoCapture: VideoCapture
    private var takingPicture = false
    private var outputFilePath: String? = null
    private val deniedPermission = ArrayList<String>()
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
  //  private val rotation = Surface.ROTATION_0
    private val resolution = Size(1280, 720)
    private val rational = AspectRatio.RATIO_16_9
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var broadcastManager: LocalBroadcastManager
    private var displayId: Int = -1
    private val RECORDED_FILE_NAME = "recorded_video"
    private val RECORDED_FILE_NAME_END = "video/mp4"

    companion object {
        private const val TAG = "CaptureActivity"
        const val PERMISSION_CODE = 1000
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        const val RESULT_FILE_PATH = "file_path"
        const val RESULT_FILE_WIDTH = "file_width"
        const val RESULT_FILE_HEIGHT = "file_height"
        const val RESULT_FILE_TYPE = "file_type"

    }


    override fun initView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel
        binding.listener=RecordListener()
        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(this)
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE)
        binding.photoViewButton.setOnClickListener {
            takingPicture=true
            openAlbum()
        }


    }

    override fun getViewBinding(): ActivityCaptureBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_capture)
    }

    override fun getViewModel(): CaptureViewModel? {
        return ViewModelProvider(this).get(CaptureViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
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
                binding.viewFinder.post {

                    // Keep track of the display in which this view is attached
                    displayId = binding.viewFinder.display.displayId

                    // Set up the camera and its use cases
                    setUpCamera()
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
                            this@CaptureActivity,
                            deniedPermission.toArray<String>(denied),
                            PERMISSION_CODE
                        )
                    }.create().show()
            }
        }
    }


    /** Initialize CameraX, and prepare to bind the camera use cases  */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("RestrictedApi")
    private fun bindCameraUseCases() {
      //  val metrics = windowManager.currentWindowMetrics.bounds
        val rotation = binding.viewFinder.display.rotation
        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        //设置camera配置
        val mCameraSelector =
            CameraSelector.Builder().requireLensFacing(lensFacing).build()

        //获得预览配置
        val mPreview: Preview = Preview.Builder()
            .setCameraSelector(mCameraSelector)
          //  .setTargetResolution(resolution)
            .setTargetRotation(rotation)
            .setTargetAspectRatio(rational)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCameraSelector(mCameraSelector)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(rational)
            .setTargetRotation(rotation)
            .build()

        // VideoCapture
        videoCapture = VideoCapture.Builder()
            .setTargetRotation(rotation)
            .setCameraSelector(mCameraSelector)
         //   .setTargetResolution(resolution)
            .setTargetAspectRatio(rational) //视频帧率
            .setVideoFrameRate(25) //bit率
            .setBitRate(3 * 1024 * 1024).build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(rational)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                    // Values returned from our analyzer are passed to the attached listener
                    // We log image analysis results here - you should do something useful
                    // instead!
                    Log.d(TAG, "Average luminosity: $luma")
                })
            }

        cameraProvider.unbindAll()
        val useCaseGroup = UseCaseGroup.Builder()   //因为CameraX不能添加太多的userCase，不然会报错，只能把imageAnalyzer先给去掉,
                                                   //由于摄像机硬件管道的定标器单元限制，对同时输出的数量存在限制。通常，在高端设备之外不支持3个并行YUV输出
                                                  //如果不支持这么多，则需要注释掉一部分
            .addUseCase(mPreview)
         //  .addUseCase(imageAnalyzer!!)
            .addUseCase(imageCapture!!)
            .addUseCase(videoCapture)
            .build()
        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, mCameraSelector, useCaseGroup)
            mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }



    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        try {
            binding.cameraSwitchButton.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            binding.cameraSwitchButton.isEnabled = false
        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
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

            .start(this)
    }

    private fun getDestinationUri(): Uri {
        val cropFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            System.currentTimeMillis().toString() + ".jpg")
        println("图片地址：${cropFile.path}")
        outputFilePath= cropFile.absolutePath
        return Uri.fromFile(cropFile)
    }


    private fun onFileSaved(file: File) {
        outputFilePath = file.absolutePath
        Log.d("TAG",outputFilePath.toString())
        val mimeType = if (takingPicture) "image/jpg" else "video/mp4"
        MediaScannerConnection.scanFile(
            this,
            arrayOf<String>(outputFilePath!!),
            arrayOf(mimeType),
            null
        )
        //拍照或者录像完成打开预览界面
        PreviewActivity.startActivityForResult(this, outputFilePath, !takingPicture, "完成")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode ){
                REQ_ALBUM->{
                    doCrop(data?.data!!)
                }

                PreviewActivity.REQ_PREVIEW->{
                    val intent = Intent()
                    intent.putExtra(RESULT_FILE_PATH, outputFilePath)
                    //当设备处于竖屏情况时，宽高的值 需要互换，横屏不需要
                    intent.putExtra(RESULT_FILE_WIDTH, resolution.height)
                    intent.putExtra(RESULT_FILE_HEIGHT, resolution.width)
                    intent.putExtra(RESULT_FILE_TYPE, !takingPicture)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                UCrop.REQUEST_CROP->{
                    val intent = Intent()
                    intent.putExtra(RESULT_FILE_PATH, outputFilePath)
                    //当设备处于竖屏情况时，宽高的值 需要互换，横屏不需要
                    intent.putExtra(RESULT_FILE_WIDTH, resolution.height)
                    intent.putExtra(RESULT_FILE_HEIGHT, resolution.width)
                    intent.putExtra(RESULT_FILE_TYPE, !takingPicture)
                    setResult(RESULT_OK, intent)
                    finish()
                }


            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    inner class RecordListener:RecordView.onRecordListener{
        override fun onClick() {
            takingPicture = true

            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                val photoFile=  File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    System.currentTimeMillis().toString() + ".jpg"
                )

                // Setup image capture metadata
                val metadata = Metadata().apply {
                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $savedUri")

                            // We can only change the foreground Drawable using API level 23+ API
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // Update the gallery thumbnail with latest picture taken
                                //  setGalleryThumbnail(savedUri)
                                // onFileSaved(photoFile)
                                doCrop(savedUri)
                            }
                            // If the folder selected is an external media directory, this is
                            // unnecessary but otherwise other apps will not be able to access our
                            // images unless we scan them using [MediaScannerConnection]
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                this@CaptureActivity,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(TAG, "Image capture scanned into media store: $uri")
                            }
                        }
                    })

                // We can only change the foreground Drawable using API level 23+ API

            }
        }

        @SuppressLint("RestrictedApi")
        override fun onLongClick() {
            println("开始了录制")
            takingPicture=false
            // Create output file to hold the image
            val videoFile=  File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                System.currentTimeMillis().toString() + ".mp4"
            )
            val contentValues=ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                RECORDED_FILE_NAME + "_" + System.currentTimeMillis()
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,RECORDED_FILE_NAME_END)

            val outputFileOptions = VideoCapture.OutputFileOptions.Builder(
                contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues
            ).build()
            println("文件名称：")
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                toastShort("请先允许相关权限获取！")
                false
            }
            videoCapture.startRecording(outputFileOptions,cameraExecutor,object :VideoCapture.OnVideoSavedCallback{
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    println("录制成功")
                   val file= uriToFileApiQ(outputFileResults.savedUri,this@CaptureActivity)
                    if (file != null) {
                        onFileSaved(file)
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e(TAG, "Video capture failed: $message",cause)
                }

            })
            true
        }

        @SuppressLint("RestrictedApi")
        override fun onFinish() {
            videoCapture.stopRecording()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun uriToFileApiQ(uri: Uri?, context: Context): File? {
        var file: File? = null
        if (uri == null) return file
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver: ContentResolver = context.contentResolver
            val displayName: String =
                ((System.currentTimeMillis() + ((Math.random() + 1) * 1000).roundToInt()).toString()
                        + "." + MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(contentResolver.getType(uri)))
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val cache: File = File(context.cacheDir.absolutePath, displayName)
                val fos = FileOutputStream(cache)
             //   FileUtils.copy(inputStream!!, fos)
                inputStream?.copyTo(fos)
                file = cache
                fos.close()
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }



}