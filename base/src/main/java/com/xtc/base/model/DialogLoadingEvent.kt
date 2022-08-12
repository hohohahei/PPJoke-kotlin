package com.xtc.base.model

data class DialogLoadingEvent(
    var loadingMsg : String,
    var loadingState : Boolean = false
)
