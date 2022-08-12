package com.example.ppjoke.ui.couch

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.ppjoke.bean.CouchTabBean
import com.xtc.base.BaseViewModel

open class CouchViewModel : BaseViewModel() {

    val fragment = ObservableField<Fragment>()
    val tabConfig: ObservableField<CouchTabBean> = ObservableField<CouchTabBean>()
    val destroy = ObservableBoolean()
    val type= ObservableInt()
     init {
         destroy.set(false)
     }


}