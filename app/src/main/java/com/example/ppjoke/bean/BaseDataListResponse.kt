package com.example.ppjoke.bean

data class BaseDataListResponse<T>(
    val `data`: BaseListResponse<T>
)