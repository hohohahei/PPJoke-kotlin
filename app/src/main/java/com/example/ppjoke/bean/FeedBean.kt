package com.example.ppjoke.bean

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.parcel.Parcelize
import org.w3c.dom.Comment


@Parcelize
class FeedBean(
    val activityIcon: String?=null,
    val activityText: String?=null,
    val author: UserBean?=null,
    val authorId: Long?=null,
    val cover: String?=null,
    val createTime: Long?=null,
    val duration: Double?=null,
    var feeds_text: String?=null,
    val height: Int?=0,
    val id: Int,
    val itemId: Long?=null,
    override val itemType: Int,
    val topComment: CommentBean?=null,
    @Bindable
    var ugc: UgcBean,
    val url: String?=null,
    val width: Int?=0
) : Parcelable, MultiItemEntity, BaseObservable(){

}
//@Parcelize
//class FeedBean: BaseObservable(), Parcelable,MultiItemEntity {
//    var id = 0
//    var itemId: Long = 0
//    override var itemType = 0
//    var createTime: Long = 0
//    var duration = 0.0
//    var feeds_text: String? = null
//    var authorId: Long = 0
//    var activityIcon: String? = null
//    var activityText: String? = null
//    var width = 0
//    var height = 0
//    var url: String? = null
//    var cover: String? = null
//    var author: UserBean? = null
//    var topComment: CommentBean? = null
//    private var ugc: UgcBean? = null
//
//    @Bindable
//    fun getUgc(): UgcBean? {
//        return this.ugc
//    }
//}

