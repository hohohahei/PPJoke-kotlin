package com.example.ppjoke.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserBean(
    val avatar: String?=null,
    val commentCount: Int?=null,
    val description: String?=null,
    val expires_time: Long?=null,
    val favoriteCount: Int?=null,
    val feedCount: Int?=null,
    val followCount: Int?=null,
    val followerCount: Int?=null,
    val hasFollow: Boolean?=null,
    val historyCount: Int?=null,
    val id: Int?=null,
    val likeCount: Int?=null,
    val name: String?=null,
    val qqOpenId: String?=null,
    val score: Int?=null,
    val topCommentCount: Int?=null,
    val userId: Long?=null
) : Parcelable