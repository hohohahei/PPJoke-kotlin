package com.example.ppjoke.ui.publish

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.ppjoke.R
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.databinding.FragmentPublishBinding
import com.example.ppjoke.ui.capture.CaptureActivity
import com.example.ppjoke.ui.capture.PreviewActivity
import com.example.ppjoke.utils.FileUtils
import com.example.ppjoke.widget.dialog.BottomTagDialog
import com.example.ppjoke.widget.dialog.LoadingDialog
import com.xtc.base.BaseMvvmFragment
import com.xtc.base.utils.toastShort
import java.util.*


class PublishFragment:BaseMvvmFragment<FragmentPublishBinding,PublishViewModel>(),View.OnClickListener {
    private var coverUploadUUID: UUID? = null
    private  var fileUploadUUID:UUID? = null
    private var coverUploadUrl: String? = null
    private  var fileUploadUrl:String? = null
    override fun getViewModel(): PublishViewModel? {
         return ViewModelProvider(this).get(PublishViewModel::class.java)
    }

    override fun initView() {
        binding?.lifecycleOwner=this
        binding?.viewModel=mViewModel
        binding!!.actionAddTag.setOnClickListener(this)
        binding!!.actionAddFile.setOnClickListener(this)
        binding!!.actionDeleteFile.setOnClickListener(this)
        binding!!.cover.setOnClickListener(this)
        binding!!.actionPublish.setOnClickListener(this)
    }

    private val mFragmentLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){activityResult->
       println("返回的结果：${activityResult.resultCode}")
//        println("返回的数据：${activityResult.data!!.getStringExtra(CaptureActivity.RESULT_FILE_PATH)}")
        if(activityResult.resultCode== Activity.RESULT_OK){
            val filePath = activityResult.data!!.getStringExtra(CaptureActivity.RESULT_FILE_PATH)
//          width =activityResult.data!!.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0)
//          height = activityResult.data!!.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0)
            val isVideo =activityResult.data!!.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false)
            mViewModel!!.addFile.value=true
            mViewModel!!.filePath.value=filePath
            mViewModel!!.isVideo.value=isVideo
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_publish
    }

    override fun addObserve() {
        super.addObserve()
        mViewModel!!.publishStatus.observe(viewLifecycleOwner){
            if(!it){
                toastShort("上传失败")
            }else{
                toastShort("上传成功")
                mViewModel!!.inputText.value=null
                mViewModel!!.addTagText.value=null
                mViewModel!!.filePath.value=null
                mViewModel!!.isVideo.value=false
                mViewModel!!.addFile.value=false

            }
            dismissLoading()
        }
    }
    private var mLoadingDialog: LoadingDialog? = null

    private fun showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(requireContext())
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
            runOnUiThread {
                if (mLoadingDialog != null) {
                    mLoadingDialog!!.dismiss()
                }
            }
        }
    }

    private fun enqueue(workRequests: List<OneTimeWorkRequest>) {
        val workContinuation = WorkManager.getInstance(requireContext()).beginWith(workRequests)
        workContinuation.enqueue()
        workContinuation.workInfosLiveData.observe(
            viewLifecycleOwner
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
                mViewModel!!.publish(
                    coverUploadUrl,
                    fileUploadUrl,
                    tagBean = mViewModel!!.tagBean.value,
                    inputText = mViewModel!!.inputText.value?:"",
                    isVideo = mViewModel!!.isVideo.value?:false
                )
            } else if (failedCount > 0) {
                dismissLoading()
            }
        }
    }

    private fun publish() {
        showLoading()
        val workRequests: MutableList<OneTimeWorkRequest> = ArrayList()
        if (!TextUtils.isEmpty(mViewModel!!.filePath.value)) {
            if (mViewModel!!.isVideo.value!!) {
                //生成视频封面文件
                FileUtils.generateVideoCover(mViewModel!!.filePath.value).observe(this,
                    androidx.lifecycle.Observer<String?> { coverPath ->
                        val request = getOneTimeWorkRequest(coverPath!!)
                        coverUploadUUID = request.id
                        workRequests.add(request)
                        enqueue(workRequests)
                    })
            }
            val request = getOneTimeWorkRequest(mViewModel!!.filePath.value!!)
            fileUploadUUID = request.id
            workRequests.add(request)
            //如果是视频文件则需要等待封面文件生成完毕后再一同提交到任务队列
            //否则 可以直接提交了
            if (!mViewModel!!.isVideo.value!!) {
                enqueue(workRequests)
            }
        } else {
            mViewModel!!.publish(
                coverUploadUrl,
                fileUploadUrl,
                tagBean = mViewModel!!.tagBean.value,
                inputText = mViewModel!!.inputText.value?:"",
                isVideo = mViewModel!!.isVideo.value?:false
            )
        }
    }


    @SuppressLint("RestrictedApi")
    private fun getOneTimeWorkRequest(filePath: String): OneTimeWorkRequest {
        val inputData: Data =Data.Builder()
            .putString("file", filePath)
            .build()

//        @SuppressLint("RestrictedApi") Constraints constraints = new Constraints();
//        //设备存储空间充足的时候 才能执行 ,>15%
//        constraints.setRequiresStorageNotLow(true);
//        //必须在执行的网络条件下才能好执行,不计流量 ,wifi
//        constraints.setRequiredNetworkType(NetworkType.UNMETERED);
//        //设备的充电量充足的才能执行 >15%
//        constraints.setRequiresBatteryNotLow(true);
//        //只有设备在充电的情况下 才能允许执行
//        constraints.setRequiresCharging(true);
//        //只有设备在空闲的情况下才能被执行 比如息屏，cpu利用率不高
//        constraints.setRequiresDeviceIdle(true);
//        //workmanager利用contentObserver监控传递进来的这个uri对应的内容是否发生变化,当且仅当它发生变化了
//        //我们的任务才会被触发执行，以下三个api是关联的
//        constraints.setContentUriTriggers(null);
//        //设置从content变化到被执行中间的延迟时间，如果在这期间。content发生了变化，延迟时间会被重新计算
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
//        constraints.setTriggerContentUpdateDelay(0);
//        //设置从content变化到被执行中间的最大延迟时间
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
//        constraints.setTriggerMaxContentDelay(0);
        return OneTimeWorkRequest.Builder(UploadFileWork::class.java)
            .setInputData(inputData) //                .setConstraints(constraints)
            //                //设置一个拦截器，在任务执行之前 可以做一次拦截，去修改入参的数据然后返回新的数据交由worker使用
            //                .setInputMerger(null)
            //                //当一个任务被调度失败后，所要采取的重试策略，可以通过BackoffPolicy来执行具体的策略
            //                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            //                //任务被调度执行的延迟时间
            //                .setInitialDelay(10, TimeUnit.SECONDS)
            //                //设置该任务尝试执行的最大次数
            //                .setInitialRunAttemptCount(2)
            //                //设置这个任务开始执行的时间
            //                //System.currentTimeMillis()
            //                .setPeriodStartTime(0, TimeUnit.SECONDS)
            //                //指定该任务被调度的时间
            //                .setScheduleRequestedAt(0, TimeUnit.SECONDS)
            //                //当一个任务执行状态编程finish时，又没有后续的观察者来消费这个结果，难么workamnager会在
            //                //内存中保留一段时间的该任务的结果。超过这个时间，这个结果就会被存储到数据库中
            //                //下次想要查询该任务的结果时，会触发workmanager的数据库查询操作，可以通过uuid来查询任务的状态
            //                .keepResultsForAtLeast(10, TimeUnit.SECONDS)
            .build()
    }

    override fun onClick(v: View?) {
        when(v){
            binding!!.actionAddTag->{
                val dialog= BottomTagDialog()
                dialog.setOnTagItemSelectedListener(object : BottomTagDialog.OnTagItemSelectedListener{
                    override fun onTagItemSelected(tagBean: TagBean) {
                        mViewModel!!.tagBean.value=tagBean
                        mViewModel!!.addTagText.value=tagBean.title
                    }

                })
                dialog.show(childFragmentManager,"tag_dialog")
            }
            binding!!.actionAddFile->{
                val intent=Intent(requireActivity(),CaptureActivity::class.java)
                mFragmentLauncher.launch(intent)
            }
            binding!!.actionDeleteFile->{
                mViewModel!!.filePath.value=null
                mViewModel!!.isVideo.value=false
                mViewModel!!.addFile.value=false
            }
            binding!!.cover->{
                PreviewActivity.startActivityForResult(
                    requireActivity(),
                    mViewModel!!.filePath.value,
                    mViewModel!!.isVideo.value?:false,
                    null
                )
            }
            binding!!.actionPublish->{
                println("点击了")
                publish()
            }
        }
    }
}