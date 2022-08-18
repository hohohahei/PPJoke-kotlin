package com.example.ppjoke.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    /**
     * 截取视频文件的封面图
     *
     * @param filePath
     * @return
     */
    @SuppressLint("RestrictedApi")
    fun generateVideoCover(filePath: String?): LiveData<String?> {
        val liveData = MutableLiveData<String?>()
        ArchTaskExecutor.getIOThreadExecutor().execute {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val frame = retriever.frameAtTime  //getFrameAtTime 方法捕获视频帧图,用于视频的封面图
            var fos: FileOutputStream? = null
            if (frame != null) {
                //压缩到200k以下，再存储到本地文件中
                val bytes = compressBitmap(frame, 200)
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    System.currentTimeMillis().toString() + ".jpeg"
                )
                try {
                    file.createNewFile()
                    fos = FileOutputStream(file)
                    fos.write(bytes)
                    liveData.postValue(file.absolutePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush()
                            fos.close()
                            fos = null
                        } catch (ignore: IOException) {
                            ignore.printStackTrace()
                        }
                    }
                }
            } else {
                liveData.postValue(null)
            }
        }
        return liveData
    }

    //循环压缩
    private fun compressBitmap(frame: Bitmap?, limit: Int): ByteArray? {
        if (frame != null && limit > 0) {
            var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
            var options = 100
            frame.compress(Bitmap.CompressFormat.JPEG, options, baos)
            while (baos!!.toByteArray().size > limit * 1024) {
                baos.reset()
                options -= 5
                frame.compress(Bitmap.CompressFormat.JPEG, options, baos)
            }
            val bytes = baos.toByteArray()
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                baos = null
            }
            return bytes
        }
        return null
    }
}
