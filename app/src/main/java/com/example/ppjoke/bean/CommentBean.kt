package com.example.ppjoke.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentBean(
    val author: UserBean?=null,
    var commentCount: Int?=null,
    val commentId: Long?=null,
    val commentText: String?=null,
    val commentType: Int?=null,
    val createTime: Long?=null,
    var hasLiked: Boolean?=null,
    val height: Int?=null,
    val id: Long?=null,
    val imageUrl: String?=null,
    val itemId: Long?=null,
    var likeCount: Int?=null,
    val ugc: UgcBean?=null,
    val userId: Long?=null,
    val videoUrl: String?=null,
    val width: Int?=null
) : Parcelable