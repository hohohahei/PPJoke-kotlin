package com.example.ppjoke.bean

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.ppjoke.BR
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
    var hasFollow: Boolean?=null,
    val historyCount: Int?=null,
    val id: Int?=null,
    val likeCount: Int?=null,
    val name: String?=null,
    val qqOpenId: String?=null,
    val score: Int?=null,
    val topCommentCount: Int?=null,
    val userId: Long?=null
) : Parcelable
//class UserBean() : Parcelable, BaseObservable(){
//    var avatar: String?=null
//    var commentCount: Int?=null
//    var description: String?=null
//    var expires_time: Long?=null
//    var favoriteCount: Int?=null
//    var feedCount: Int?=null
//    var followCount: Int?=null
//    var followerCount: Int?=null
//    var hasFollow: Boolean?=null
//    var historyCount: Int?=null
//    var id: Int?=null
//    var likeCount: Int?=null
//    var name: String?=null
//    var qqOpenId: String?=null
//    var score: Int?=null
//    var topCommentCount: Int?=null
//    var userId: Long?=null
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    constructor(parcel: Parcel) : this() {
//        avatar=parcel.readString()
//        commentCount=parcel.readInt()
//        description=parcel.readString()
//        expires_time=parcel.readLong()
//        favoriteCount=parcel.readInt()
//        feedCount=parcel.readInt()
//        followCount=parcel.readInt()
//        followerCount=parcel.readInt()
//        hasFollow=parcel.readBoolean()
//        historyCount=parcel.readInt()
//        id=parcel.readInt()
//        likeCount=parcel.readInt()
//        name=parcel.readString()
//        qqOpenId=parcel.readString()
//        score=parcel.readInt()
//        topCommentCount=parcel.readInt()
//        userId=parcel.readLong()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun writeToParcel(parcel: Parcel, flags:Int) {
//        avatar?.let {parcel.writeString(it)}
//        commentCount?.let {parcel.writeInt(it)}
//        description?.let {parcel.writeString(it)}
//        expires_time?.let {parcel.writeLong(it)}
//        favoriteCount?.let {parcel.writeInt(it)}
//        feedCount?.let {parcel.writeInt(it)}
//        followCount?.let {parcel.writeInt(it)}
//        followerCount?.let {parcel.writeInt(it)}
//        hasFollow?.let{parcel.writeBoolean(it)}
//        historyCount?.let {parcel.writeInt(it)}
//        id?.let {parcel.writeInt(it)}
//        likeCount?.let {parcel.writeInt(it)}
//        name?.let {parcel.writeString(it)}
//        qqOpenId?.let {parcel.writeString(it)}
//        score?.let {parcel.writeInt(it)}
//        topCommentCount?.let {parcel.writeInt(it)}
//        userId?.let {parcel.writeLong(it)}
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<UserBean> {
//        @RequiresApi(Build.VERSION_CODES.Q)
//        override fun createFromParcel(parcel: Parcel): UserBean {
//            return UserBean(parcel)
//        }
//
//        override fun newArray(size: Int): Array<UserBean?> {
//            return arrayOfNulls(size)
//        }
//    }
//}