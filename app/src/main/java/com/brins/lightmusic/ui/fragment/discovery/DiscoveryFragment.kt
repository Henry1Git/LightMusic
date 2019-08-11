package com.brins.lightmusic.ui.fragment.discovery


import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.brins.lightmusic.R
import com.brins.lightmusic.model.artist.ArtistBean
import com.brins.lightmusic.model.onlinemusic.MusicListBean
import com.brins.lightmusic.model.onlinemusic.MusicListDetailBean
import com.brins.lightmusic.model.onlinemusic.OnlineMusic
import com.brins.lightmusic.ui.activity.MainActivity
import com.brins.lightmusic.ui.base.BaseFragment
import com.brins.lightmusic.ui.customview.PileLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_discovery.*
import java.lang.Exception

class DiscoveryFragment : BaseFragment(), DiscoveryContract.View {

    lateinit var mPresenter: DiscoveryContract.Presenter
    lateinit var artistlist: MutableList<ArtistBean>
    lateinit var musicListBean: MutableList<MusicListBean>


    override fun onLazyLoad() {
        super.onLazyLoad()
        getArtist()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getArtist() {
        DiscoverPresent(this@DiscoveryFragment).subscribe()
    }

    //MVP View

    override fun onDetailLoad(detailBean: MusicListDetailBean) {

    }

    override fun showLoading() {
        loadingLayout.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingLayout.visibility = View.GONE
    }

    override fun getLifeActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getcontext(): Context {
        return context!!
    }


    override fun handleError(error: Throwable) {

    }

    override fun onMusicListLoad(songs: MutableList<MusicListBean>) {
        musicListBean = songs
        initMusicList()
    }

    override fun onArtistLoad(artistBeans: MutableList<ArtistBean>) {
        artistlist = artistBeans
        initArtistView()
    }

    override fun onMusicDetail(onlineMusic: OnlineMusic) {}

    private fun initArtistView() {
        pileLayout.visibility = View.VISIBLE
        pileLayout.setAdapter(object : PileLayout.Adapter() {
            override fun getLayoutId(): Int {
                return R.layout.artist_item
            }

            override fun getItemCount(): Int {
                return artistlist.size
            }

            override fun bindView(view: View, index: Int) {
                var viewHolder: ViewHolder? = view.tag as ViewHolder?
                if (viewHolder == null) {
                    viewHolder = ViewHolder()
                    viewHolder.imageView = view.findViewById(R.id.iv_recovery)
                    viewHolder.textView = view.findViewById(R.id.introduce)
                    view.tag = viewHolder
                }
                viewHolder.textView!!.text = artistlist[index].name
                Glide.with(this@DiscoveryFragment)
                    .load(artistlist[index].picUrl)
                    .into(viewHolder.imageView!!)
            }
        })

    }

    private fun initMusicList() {
        val adapter = MusicListAdapter(
            MusicListAdapter.TYPE_MUSIC_LIST,
            context!!,
            musicListBean
        )
        adapter.setOnItemClickListener(object : MusicListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val id = musicListBean[position].id
                try {
                     (activity as MainActivity).switchFragment(id,MusicDetailFragment.Instance)
                         .addToBackStack(TAG)
                         .commit()
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            }

        })
        recycleMusiclist.adapter = adapter
        recycleMusiclist.layoutManager = LinearLayoutManager(context!!)

    }

    override fun setPresenter(presenter: DiscoveryContract.Presenter?) {
        mPresenter = presenter!!
    }

    override fun getLayoutResID(): Int {
        return R.layout.fragment_discovery
    }

    class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }

}
