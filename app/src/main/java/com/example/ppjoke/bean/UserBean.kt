package com.example.ppjoke.bean

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.ppjoke.BR
import kotlinx.android.parcel.Parcelize

//@Parcelize
//data class UserBean(
//    var avatar: String?=null,
//    val commentCount: Int?=null,
//    var description: String?=null,
//    val expires_time: Long?=null,
//    val favoriteCount: Int?=null,
//    val feedCount: Int?=null,
//    val followCount: Int?=null,
//    val followerCount: Int?=null,
//    var hasFollow: Boolean?=null,
//    val historyCount: Int?=null,
//    val id: Int?=null,
//    val likeCount: Int?=null,
//    var name: String?=null,
//    val qqOpenId: String?=null,
//    val score: Int?=null,
//    val topCommentCount: Int?=null,
//    val userId: Long?=null
//) : Parcelable
class UserBean() : Parcelable, BaseObservable() {
    var avatar: String? = null
    var commentCount: Int? = null
    var description: String? = null
    var expires_time: Long? = null
    var favoriteCount: Int? = null
    var feedCount: Int? = null
    var followCount: Int? = null
    var followerCount: Int? = null
    var hasFollow: Boolean? = null
    var historyCount: Int? = null
    var id: Int? = null
    var likeCount: Int? = null
    var name: String? = null
    var qqOpenId: String? = null
    var score: Int? = null
    var topCommentCount: Int? = null
    var userId: Long? = null

    constructor(parcel: Parcel) : this() {
        avatar = parcel.readString()
        commentCount = parcel.readValue(Int::class.java.classLoader) as? Int
        description = parcel.readString()
        expires_time = parcel.readValue(Long::class.java.classLoader) as? Long
        favoriteCount = parcel.readValue(Int::class.java.classLoader) as? Int
        feedCount = parcel.readValue(Int::class.java.classLoader) as? Int
        followCount = parcel.readValue(Int::class.java.classLoader) as? Int
        followerCount = parcel.readValue(Int::class.java.classLoader) as? Int
        hasFollow = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        historyCount = parcel.readValue(Int::class.java.classLoader) as? Int
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        likeCount = parcel.readValue(Int::class.java.classLoader) as? Int
        name = parcel.readString()
        qqOpenId = parcel.readString()
        score = parcel.readValue(Int::class.java.classLoader) as? Int
        topCommentCount = parcel.readValue(Int::class.java.classLoader) as? Int
        userId = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeValue(commentCount)
        parcel.writeString(description)
        parcel.writeValue(expires_time)
        parcel.writeValue(favoriteCount)
        parcel.writeValue(feedCount)
        parcel.writeValue(followCount)
        parcel.writeValue(followerCount)
        parcel.writeValue(hasFollow)
        parcel.writeValue(historyCount)
        parcel.writeValue(id)
        parcel.writeValue(likeCount)
        parcel.writeString(name)
        parcel.writeString(qqOpenId)
        parcel.writeValue(score)
        parcel.writeValue(topCommentCount)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBean> {
        override fun createFromParcel(parcel: Parcel): UserBean {
            return UserBean(parcel)
        }

        override fun newArray(size: Int): Array<UserBean?> {
            return arrayOfNulls(size)
        }
    }
}

