package com.brins.lightmusic.ui.fragment.discovery


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brins.lightmusic.R
import com.brins.lightmusic.model.banner.Banner
import com.brins.lightmusic.model.onlinemusic.MusicListBean
import com.brins.lightmusic.ui.activity.MainActivity
import com.brins.lightmusic.ui.base.BaseFragment
import com.brins.lightmusic.utils.launch
import kotlinx.android.synthetic.main.fragment_discovery.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscoveryFragment : BaseFragment<DiscoveryContract.Presenter>(), DiscoveryContract.View,
    SwipeRefreshLayout.OnRefreshListener, DiscoveryAdapter.OnItemClickListener {


    private var isFresh: Boolean = false
    private var count: Int = 1
    private lateinit var mPresenter: DiscoveryContract.Presenter
    private lateinit var bannerList: ArrayList<Banner>
    private lateinit var musicListBean: ArrayList<MusicListBean>
    private lateinit var musicHotListBean: ArrayList<MusicListBean>
    private val bannerAdapter by lazy {
        DiscoveryAdapter(DiscoveryAdapter.TYPE_BANNER, bannerList)
    }
    private val musicListAdapter by lazy {
        DiscoveryAdapter(DiscoveryAdapter.TYPE_MUSIC_LIST, musicListBean)
    }

    private val musicHotAdapter by lazy {
        DiscoveryAdapter(DiscoveryAdapter.TYPE_MUSIC_LIST, musicHotListBean)

    }

    override fun onLazyLoad() {
        super.onLazyLoad()
        getBanner()
    }

    private fun getBanner() {
        DiscoverPresenter.instance.subscribe(this@DiscoveryFragment)
        showLoading()
        initLoadingMore()
        initBannerList()
        initMusicList()
        initHotMusicList()
    }
    /*
    * 获取横幅广告
    * */
    private fun initBannerList() {
        launch({
            bannerList = getBannerData().bannners!!
            initBannerView()
        }, {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        })
    }

    private suspend fun getBannerData() = withContext(Dispatchers.IO) {
        val banner = mPresenter.loadBanner()
        banner
    }

    private fun initBannerView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerBanner.layoutManager = layoutManager
        recyclerBanner.adapter = bannerAdapter
    }

    /*
    * 获取热门歌单
    * */
    private fun initHotMusicList(limit: Int = 6) {
        launch({
            val musicHotList = getHotMusicList(limit).playlists as ArrayList<MusicListBean>
            if (musicHotList.size > 6) {
                musicHotListBean.clear()
                musicHotListBean.addAll(
                    musicHotList.subList(
                        musicHotList.size - 6,
                        musicHotList.size
                    )
                )
            } else {
                musicHotListBean = musicHotList
            }
            initHotMusicView()
        }, {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()

        })
    }

    private suspend fun getHotMusicList(limit: Int) = withContext(Dispatchers.IO) {
        val musiclist = mPresenter.loadHotMusicList(limit)
        musiclist
    }

    private fun initHotMusicView() {
        musicHotAdapter.setOnItemClickListener(this)
        recycleMusiclist2.adapter = musicHotAdapter
        recycleMusiclist2.layoutManager = GridLayoutManager(context!!, 3)
        hideLoading()
    }

    /*
    * 获取歌单
    * */
    private fun initMusicList(limit: Int = 6) {
        launch({
            val musicList = getMusicList(limit).playlists as ArrayList<MusicListBean>
            if (musicList.size > 6) {
                musicListBean.clear()
                musicListBean.addAll(musicList.subList(musicList.size - 6, musicList.size))
            } else {
                musicListBean = musicList
            }
            initMusicView()
        }, {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        })
    }

    private suspend fun getMusicList(limit: Int) = withContext(Dispatchers.IO) {
        val musicList = mPresenter.loadMusicList(limit)
        musicList
    }


    private fun initMusicView() {
        musicListAdapter.setOnItemClickListener(this)
        recycleMusiclist.adapter = musicListAdapter
        recycleMusiclist.layoutManager = GridLayoutManager(context!!, 3)
        hideLoading()
    }


    private fun initLoadingMore() {
        if (loadingMore == null){
            return
        }
        loadingMore.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        loadingMore.setDistanceToTriggerSync(700)
        loadingMore.setProgressBackgroundColorSchemeColor(Color.WHITE)
        loadingMore.setSize(SwipeRefreshLayout.DEFAULT)
        loadingMore.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (!isFresh) {
            ++count
            isFresh = true
            loadingMore.isRefreshing = false
            showLoading()
            initMusicList(count * 6)
            initHotMusicList(count * 6)
        }
    }


    override fun getLayoutResID(): Int {
        return R.layout.fragment_discovery
    }

    override fun setPresenter(presenter: DiscoveryContract.Presenter) {
        mPresenter = presenter
    }

    override fun onItemClick(view: View, id: String) {
        try {
            val bundle = Bundle()
            bundle.putString(TAG, id)
            (activity as MainActivity).switchFragment(MusicDetailFragment(), bundle)
                .addToBackStack(TAG)
                .commit()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

}
