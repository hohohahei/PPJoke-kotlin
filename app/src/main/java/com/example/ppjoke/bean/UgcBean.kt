package com.example.ppjoke.bean
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.ppjoke.BR

class UgcBean() : Parcelable, BaseObservable() {
    @Bindable
    var commentCount: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.commentCount)
        }

    @Bindable
    var hasDissed: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasDissed)
        }

    @Bindable
    var hasFavorite: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasFavorite)
        }

    @Bindable
    var hasLiked: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasLiked)
        }

    @Bindable
    var hasdiss: Boolean? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasdiss)
        }

    @Bindable
    var likeCount: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.likeCount)
        }

    @Bindable
    var shareCount: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.shareCount)
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this() {
        commentCount = parcel.readInt()
        likeCount = parcel.readInt()
        shareCount = parcel.readInt()
        hasDissed = parcel.readBoolean()
        hasFavorite = parcel.readBoolean()
        hasLiked = parcel.readBoolean()
        hasdiss = parcel.readBoolean()
    }

    override fun describeContents(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        commentCount?.let { parcel.writeInt(it) }
        likeCount?.let { parcel.writeInt(it) }
        shareCount?.let { parcel.writeInt(it) }
        hasDissed?.let { parcel.writeBoolean(it) }
        hasFavorite?.let { parcel.writeBoolean(it) }
        hasLiked?.let { parcel.writeBoolean(it) }
        hasdiss?.let { parcel.writeBoolean(it) }
    }

    companion object CREATOR : Parcelable.Creator<UgcBean> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): UgcBean {
            return UgcBean(parcel)
        }

        override fun newArray(size: Int): Array<UgcBean?> {
            return arrayOfNulls(size)
        }
    }
}