package com.example.ppjoke.bean

data class CouchTabBean(
    val activeColor: String,
    val activeSize: Int,
    val normalColor: String,
    val normalSize: Int,
    val select: Int,
    val tabGravity: Int,
    val tabs: List<TabBean>
)