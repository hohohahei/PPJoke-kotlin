package com.example.ppjoke.http

import com.example.ppjoke.bean.*
import com.xtc.base.model.BaseBean
import retrofit2.http.*

interface ApiService {
    @GET(ApiUrl.QUERY_FEED_LIST)
    suspend fun queryFeedsList(
        @Query("feedType",encoded = false) feedType: String,
        @Query("feedId") feedId:Int,
        @Query("pageCount") pageCount:Int,
        @Query("userId") userId:Long
   ): BaseBean<BaseListResponse<FeedBean>>

    @GET(ApiUrl.QUERY_FEED_COMMENTS)
    suspend fun queryFeedComments(
        @Query("id") id:Long,
        @Query("itemId") itemId:Long
    ):BaseBean<BaseListResponse<CommentBean>>

    @GET(ApiUrl.QUERY_TAG_LIST)
    suspend fun queryTagList(
        @Query("offset") offset:Int=0,
        @Query("pageCount") pageCount:Int=10,
        @Query("tagId") tagId:Int=0,
        @Query("tagType") tagType:String,
        @Query("userId") userId:Long
    ):BaseBean<BaseListResponse<TagBean>>

    @GET(ApiUrl.QUERY_USER)
    suspend fun queryUserInfo(
        @Query("userId") userId: Long
    ):BaseBean<BaseResponse<UserBean>>

    @GET(ApiUrl.QUERY_PROFILE_FEEDS)
    suspend fun queryProfileFeeds(
        @Query("userId") userId: Long,
        @Query("profileType") profileType: String,
        @Query("feedId") feedId: Int
    ):BaseBean<BaseListResponse<FeedBean>>

    @GET(ApiUrl.QUERY_USER_BEHAVIOR_LIST)
    suspend fun queryUserBehaviorList(
        @Query("userId") userId: Long,
        @Query("behavior") behavior:Int,
        @Query("feedId") feedId: Int
    ):BaseBean<BaseListResponse<FeedBean>>

    @GET(ApiUrl.TOGGLE_FEED_LIKE)
    suspend fun toggleFeedLike(
        @Query("userId") userId: Long,
        @Query("itemId") itemId: Long
    ):BaseBean<BaseResponse<ToggleLikeResponse>>

    @GET(ApiUrl.TOGGLE_FAVORITE)
    suspend fun toggleFeedFavorite(
        @Query("userId") userId: Long,
        @Query("itemId") itemId:Long
    ):BaseBean<BaseResponse<ToggeleFavoriteResponse>>

    @GET(ApiUrl.TOGGLE_COMMENT_LIKE)
    suspend fun toggleCommentLike(
        @Query("userId") userId: Long,
        @Query("commentId") commentId:Long
    ):BaseBean<BaseResponse<ToggleLikeResponse>>

    @GET(ApiUrl.TOGGLE_SHARE)
    suspend fun shareFeed(
        @Query("itemId") itemId: Long
    ):BaseBean<BaseResponse<ToggleFeedShareResponse>>

    @GET(ApiUrl.TOGGLE_USER_FOLLOW)
    suspend fun toggleUserFollow(
        @Query("followUserId") followUserId:Long,
        @Query("userId") userId: Long
    ):BaseBean<BaseResponse<ToggleLikeResponse>>

    @FormUrlEncoded
    @POST(ApiUrl.ADD_COMMENT)
    suspend fun addComment(
        @Field("itemId") itemId: Long,
        @Field("userId") userId: Long,
        @Field("commentText",encoded = true) commentText:String
    ):BaseBean<BaseResponse<CommentBean>>

    @FormUrlEncoded
    @POST(ApiUrl.USER_INSERT)
    suspend fun userInsert(
        @Field("name") nickName:String,
        @Field("avatar") avatar:String,
        @Field("qqOpenId") openId:String,
        @Field("expires_time") expiresTime:Long
    ):BaseBean<BaseResponse<UserBean>>

    @GET(ApiUrl.USER_RELATION)
    suspend fun queryUserRelation(
        @Query("userId") userId: Long,
        @Query("authorId") authorId:Long
    ):BaseBean<BaseResponse<UserBean>>

    @GET(ApiUrl.TOGGLE_TAG_FOLLOW)
    suspend fun toggleTagFollow(
        @Query("userId") userId: Long,
        @Query("tagId") tagId: Int
    ):BaseBean<BaseResponse<ToggleFollowResponse>>

    @FormUrlEncoded
    @POST(ApiUrl.FEED_PUBLISH)
    suspend fun feedPublish(
        @Field("coverUrl",encoded = true) coverUrl:String,
        @Field("fileUrl",encoded = true) fileUrl:String,
        @Field("fileWidth") width:Int,
        @Field("fileHeight") height:Int,
        @Field("userId") userId: Long,
        @Field("tagId") tagId: Int,
        @Field("tagTitle",encoded = true) tagTitle:String,
        @Field("feedText",encoded = true) inputText:String,
        @Field("feedType") feedType:Int,
    ):BaseBean<BaseResponse<ToggleResultResponse>>

    @GET(ApiUrl.FEED_DELETE)
    suspend fun feedDelete(
        @Query("itemId") itemId: Long
    ):BaseBean<BaseResponse<ToggleResultBooleanResp>>

    @GET(ApiUrl.DELETE_COMMENT)
    suspend fun deleteComment(
        @Query("itemId") itemId: Long,
        @Query("commentId") commentId: Long,
        @Query("userId") userId: Long
    ):BaseBean<BaseResponse<ToggleResultBooleanResp>>

    @GET(ApiUrl.QUERY_FANS)
    suspend fun queryFans(
        @Query("userId") userId: Long
    ):BaseBean<BaseDataListResponse<UserBean>>

    @GET(ApiUrl.QUERY_FOLLOWS)
    suspend fun queryFollows(
        @Query("userId") userId: Long
    ):BaseBean<BaseDataListResponse<UserBean>>

    @FormUrlEncoded
    @POST(ApiUrl.UPDATE_USER)
    suspend fun updateUser(
        @Field("user") user:String
    )

}