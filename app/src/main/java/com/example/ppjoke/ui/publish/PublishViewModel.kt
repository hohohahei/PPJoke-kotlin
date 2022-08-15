package com.example.ppjoke.ui.publish

import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.TagBean
import com.example.ppjoke.repo.FeedRepo
import com.example.ppjoke.utils.MMKVUtils
import com.xtc.base.BaseViewModel

class PublishViewModel:BaseViewModel() {
    private val repo by lazy { FeedRepo() }
    var addFile = MutableLiveData<Boolean>()
    var isVideo = MutableLiveData<Boolean>()

    //上传文件路径
    var filePath = MutableLiveData<String>()

    var tagBean=MutableLiveData<TagBean>()
    //标签名
    var addTagText= MutableLiveData<String>()

    //发布内容 双向绑定
    var inputText = MutableLiveData<String>()


    val userId=MMKVUtils.getInstance().getUserId()

    var publishStatus=MutableLiveData<Boolean>()
    init {
        isVideo.value=false
    }

     fun publish(coverUploadUrl:String?, fileUploadUrl:String?, width:Int=720,
                        height:Int=1280, tagBean: TagBean?, inputText:String, isVideo:Boolean){

          val tagId=tagBean?.tagId?:0
          val tagTitle=tagBean?.title?:""
          val feedType=if(isVideo) 2 else 1
         launch {
            val response= repo.publishFeed(
                 coverUploadUrl?:"",
                 fileUploadUrl?:"",
                 width,
                 height,
                 userId ?: 0,
                 tagId,
                 tagTitle,
                 inputText,
                 feedType
             )
             publishStatus.value=response.data.result=="success"
         }
    }
}