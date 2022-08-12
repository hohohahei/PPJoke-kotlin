package com.example.ppjoke.ui.capture

import android.graphics.SurfaceTexture
import androidx.lifecycle.MutableLiveData
import com.xtc.base.BaseViewModel

class CaptureViewModel:BaseViewModel() {
    val tipVisibility=MutableLiveData<Boolean>()
    val surfaceTexture=MutableLiveData<SurfaceTexture>()

    init {
        tipVisibility.value=true
    }
}