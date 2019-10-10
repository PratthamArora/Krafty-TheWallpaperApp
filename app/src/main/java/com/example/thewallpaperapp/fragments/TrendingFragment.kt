package com.example.thewallpaperapp.fragments


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thewallpaperapp.ViewHolder.WallpaperViewHolder
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.model.WallpaperItem
import com.example.thewallpaperapp.ui.ViewWallpaperActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krafty.android.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.layout_wallpaper_item.view.*

class TrendingFragment : Fragment() {


    companion object {
        private var instance: TrendingFragment? = null
        fun getInstance(): TrendingFragment? {
            if (instance == null)
                instance = TrendingFragment()
            return instance
        }
    }
    //Firebase

    private val trendingBackground by lazy {
        FirebaseDatabase.getInstance().reference.child("Wallpapers")
    }
    private val ref by lazy {
        FirebaseDatabase.getInstance().reference.child("Wallpapers").orderByChild("viewCount")
    }
    lateinit var adapter: FirebaseRecyclerAdapter<WallpaperItem, WallpaperViewHolder>
    private var recyclerView: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_trending, container, false)

        recyclerView = myView.findViewById(R.id.recycler_trending)
        recyclerView?.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView?.layoutManager = linearLayoutManager


        loadTrending()
        recyclerView?.adapter = adapter
        return myView


    }

    private fun loadTrending() {
        val options = FirebaseRecyclerOptions.Builder<WallpaperItem>()
            .setQuery(ref, WallpaperItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = object : FirebaseRecyclerAdapter<WallpaperItem, WallpaperViewHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {

                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_wallpaper_item, parent, false)

                return WallpaperViewHolder(itemView)
            }

            override fun onBindViewHolder(
                holder: WallpaperViewHolder,
                position: Int,
                model: WallpaperItem
            ) {
                trendingBackground.child("Wallpapers")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value == null)
                                load_trending.visibility = View.GONE
                        }
                    })


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Picasso.get()
                        .load(model.imageURL)
                        .resize(
                            DisplayMetrics.DENSITY_DEVICE_STABLE,
                            DisplayMetrics.DENSITY_DEVICE_STABLE
                        )
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.itemView.walls_item_image, object : Callback {
                            override fun onSuccess() {
                                //placeholder_anim_trend.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                Picasso.get()
                                    .load(model.imageURL)
                                    .resize(
                                        DisplayMetrics.DENSITY_DEVICE_STABLE,
                                        DisplayMetrics.DENSITY_DEVICE_STABLE
                                    )
                                    .error(R.drawable.ic_terrain_black_24dp)
                                    .into(holder.itemView.walls_item_image, object : Callback {
                                        override fun onSuccess() {
                                            //placeholder_anim_trend.visibility = View.GONE
                                        }

                                        override fun onError(e: java.lang.Exception?) {
                                            Log.e("Error_image", "failed to fetch image")
                                        }

                                    })
                            }

                        })
                }

                holder.itemView.setOnClickListener {
                    Common.categoryIDSelected = adapter.getRef(position).key!!
                    Common.wallName = model.wallpaperName
                    Common.selectBackground = model
                    Common.selectBackgroundKey = adapter.getRef(position).key!!
                    val intent = Intent(activity, ViewWallpaperActivity::class.java)
                    startActivity(intent)
                }

            }

        }

    }


    override fun onStart() {
        adapter.startListening()

        super.onStart()
    }


    override fun onStop() {
        adapter.stopListening()
        super.onStop()
    }

    override fun onResume() {
        adapter.startListening()
        super.onResume()
    }

}