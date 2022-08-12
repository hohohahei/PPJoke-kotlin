package com.example.ppjoke.bean

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.ppjoke.BR


class TagBean() : Parcelable, BaseObservable() {
    var activityIcon: String?=null
    var background: String?=null
    var enterNum: Int?=null
    var feedNum: Int?=null
    var followNum: Int?=null
    @Bindable
    var hasFollow: Boolean?=null
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasFollow)
        }
    var icon: String?=null
    var id: Int?=null
    var intro: String?=null
    var tagId: Int?=null
    var title: String?=null

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this() {
        activityIcon = parcel.readString()
        background = parcel.readString()
        enterNum = parcel.readInt()
        feedNum = parcel.readInt()
        followNum = parcel.readInt()
        hasFollow = parcel.readBoolean()
        icon = parcel.readString()
        id=parcel.readInt()
        intro = parcel.readString()
        tagId=parcel.readInt()
        title = parcel.readString()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        activityIcon?.let {   parcel.writeString(it)}
        background ?.let {parcel.writeString(it)}
        enterNum ?.let {parcel.writeInt(it)}
        feedNum ?.let {parcel.writeInt(it)}
        followNum ?.let {parcel.writeInt(it)}
        hasFollow ?.let {parcel.writeBoolean(it)}
        icon ?.let {parcel.writeString(it)}
        id?.let {parcel.writeInt(it)}
        intro?.let {parcel.writeString(it)}
        tagId?.let {parcel.writeInt(it)}
        title ?.let {parcel.writeString(it)}
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagBean> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): TagBean {
            return TagBean(parcel)
        }

        override fun newArray(size: Int): Array<TagBean?> {
            return arrayOfNulls(size)
        }
    }
}