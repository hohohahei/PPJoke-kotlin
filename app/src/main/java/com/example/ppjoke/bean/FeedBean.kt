package com.example.ppjoke.bean

import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.parcel.Parcelize


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
    var ugc: UgcBean,
    val url: String?=null,
    val width: Int?=0
) : Parcelable, MultiItemEntity, BaseObservable()

