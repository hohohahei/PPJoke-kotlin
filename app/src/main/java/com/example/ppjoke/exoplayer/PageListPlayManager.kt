package com.example.ppjoke.exoplayer

import android.app.Application
import android.net.Uri
import com.example.ppjoke.AppGlobals
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util


/**
 * 能适应多个页面视频播放的 播放器管理者
 * 每个页面一个播放器
 * 方便管理每个页面的暂停/恢复操作
 */
object PageListPlayManager {
    private val sPageListPlayHashMap = HashMap<String, PageListPlay>()
    private var mediaSourceFactory: ProgressiveMediaSource.Factory? = null
    fun createMediaSource(url: String?): MediaSource {
        return mediaSourceFactory!!.createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }

    fun get(pageName: String): PageListPlay {
        var pageListPlay = sPageListPlayHashMap[pageName]
        if (pageListPlay == null) {
            pageListPlay = PageListPlay()
            sPageListPlayHashMap[pageName] = pageListPlay
        }
        return pageListPlay
    }

    fun release(pageName: String) {
        val pageListPlay = sPageListPlayHashMap.remove(pageName)
        pageListPlay?.release()
    }

    init {
        val application: Application = AppGlobals.getApplication()
        //创建http视频资源如何加载的工厂对象
        val dataSourceFactory =
            DefaultHttpDataSource.Factory()
        dataSourceFactory.setUserAgent(Util.getUserAgent(application, application.packageName))
        //创建缓存，指定缓存位置，和缓存策略,为最近最少使用原则,最大为200m
        val cache: Cache =
            SimpleCache(application.cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200))
        //把缓存对象cache和负责缓存数据读取、写入的工厂类CacheDataSinkFactory 相关联
        val cacheDataSinkFactory = CacheDataSink.Factory()
        cacheDataSinkFactory.setCache(cache)


        /*创建能够 边播放边缓存的 本地资源加载和http网络数据写入的工厂类
         * public CacheDataSourceFactory(
         *       Cache cache, 缓存写入策略和缓存写入位置的对象
         *       DataSource.Factory upstreamFactory,http视频资源如何加载的工厂对象
         *       DataSource.Factory cacheReadDataSourceFactory,本地缓存数据如何读取的工厂对象
         *       @Nullable DataSink.Factory cacheWriteDataSinkFactory,http网络数据如何写入本地缓存的工厂对象
         *       @CacheDataSource.Flags int flags,加载本地缓存数据进行播放时的策略,如果遇到该文件正在被写入数据,或读取缓存数据发生错误时的策略
         *       @Nullable CacheDataSource.EventListener eventListener  缓存数据读取的回调
         */
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setCacheWriteDataSinkFactory(cacheDataSinkFactory)
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
            .setEventListener(null)

        //最后 还需要创建一个 MediaSource 媒体资源 加载的工厂类
        //因为由它创建的MediaSource 能够实现边缓冲边播放的效果,
        //如果需要播放hls,m3u8 则需要创建DashMediaSource.Factory()
        mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    }
}
