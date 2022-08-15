package com.example.ppjoke.ui.publish

import android.content.Context
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.ppjoke.utils.FileUploadManager

class UploadFileWork(context: Context,workParams:WorkerParameters):Worker(context,workParams) {
    override fun doWork(): Result {
        val filePath=inputData.getString("file")
        val fileUrl= filePath?.let { FileUploadManager.upload(it) }
        if(TextUtils.isEmpty(fileUrl)){
            return Result.failure()
        }else{
            val outputData= Data.Builder().putString("fileUrl",fileUrl).build()
            return Result.success(outputData)
        }
    }
}