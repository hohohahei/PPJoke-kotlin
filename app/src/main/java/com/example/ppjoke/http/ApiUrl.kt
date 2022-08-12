package com.example.ppjoke.http

object ApiUrl {
    const val BASE_URL = "http://123.56.232.18:8080/serverdemo/"

    //查询帖子列表
    const val QUERY_FEED_LIST="feeds/queryHotFeedsList"
    //查询评论列表
    const val QUERY_FEED_COMMENTS="comment/queryFeedComments"
    //查询标签列表
    const val QUERY_TAG_LIST="tag/queryTagList"
    //查询用户信息
    const val QUERY_USER="user/query"
    //查询用户帖子
    const val QUERY_PROFILE_FEEDS="feeds/queryProfileFeeds"
    //
    const val QUERY_USER_BEHAVIOR_LIST="feeds/queryUserBehaviorList"
    //帖子点赞
    const val TOGGLE_FEED_LIKE = "ugc/toggleFeedLike"

    const val TOGGLE_FEED_DISS="ugc/dissFeed"
    //评论点赞
    const val TOGGLE_COMMENT_LIKE="ugc/toggleCommentLike"
    //帖子分享
    const val TOGGLE_SHARE="ugc/increaseShareCount"
    //帖子收藏
    const val TOGGLE_FAVORITE="ugc/toggleFavorite"
    //用户关注
    const val TOGGLE_USER_FOLLOW="ugc/toggleUserFollow"
    //添加评论
    const val ADD_COMMENT="comment/addComment"
    //添加用户
    const val USER_INSERT="user/insert"
    //查询用户间关系
    const val USER_RELATION="user/relation"
    //标签关注
    const val TOGGLE_TAG_FOLLOW="tag/toggleTagFollow"
}