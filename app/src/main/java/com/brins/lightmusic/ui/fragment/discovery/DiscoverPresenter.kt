package com.brins.lightmusic.ui.fragment.discovery


import com.brins.lightmusic.api.ApiHelper
import com.brins.lightmusic.utils.await
import javax.inject.Inject

class DiscoverPresenter @Inject constructor() : DiscoveryContract.Presenter {

    private var mView: DiscoveryContract.View? = null

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = DiscoverPresenter()
    }


    override suspend fun loadBanner() = ApiHelper.getDiscoveryService().getBanner().await()

    override suspend fun loadMusicList(top: Int) =
        ApiHelper.getPlayListService().getHightQualityList(top).await()

    override suspend fun loadHotMusicList(top: Int) =
        ApiHelper.getPlayListService().getPlayList(top).await()

    override suspend fun loadMusicListDetail(id: String) =
        ApiHelper.getPlayListService().getPlayListDetail(id).await()

    override suspend fun loadAlbumDetail(id: String) =
        ApiHelper.getPlayListService().getAlbumDetail(id).await()


    override fun subscribe(view: DiscoveryContract.View) {
        mView = view
    }

    override fun unsubscribe() {
        mView = null
    }
}