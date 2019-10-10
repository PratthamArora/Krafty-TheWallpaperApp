package com.example.thewallpaperapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thewallpaperapp.ViewHolder.WallpaperViewHolder
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.model.WallpaperItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.krafty.android.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.android.synthetic.main.staggered_recycler_item.view.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.startActivity

class FavouriteActivity : AppCompatActivity() {

    private val query by lazy {


        FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("favourites")


    }


    lateinit var wallAdapter: FirebaseRecyclerAdapter<WallpaperItem, WallpaperViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageurlTest: String


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        setSupportActionBar(fav_walls_toolbar)
        supportActionBar?.title = "Favourites"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerView = findViewById(R.id.fav_walls_recyclerView)

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager


        if (FirebaseAuth.getInstance().currentUser != null) {
            query.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    for (ds in p0.children) {
                        imageurlTest =
                            ds.child(Common.categorySelected).child(Common.selectBackgroundKey).child(
                                "imageURL"
                            ).getValue(String::class.java)!!
                    }
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    for (ds in p0.children) {
                        imageurlTest =
                            ds.child(Common.categorySelected).child(Common.selectBackgroundKey).child(
                                "imageURL"
                            ).getValue(String::class.java)!!
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }


            })
            loadBackgroundList()

        }


    }

    private fun loadBackgroundList() {

        val options = FirebaseRecyclerOptions.Builder<WallpaperItem>()
            .setQuery(this.query, WallpaperItem::class.java)
            .setLifecycleOwner(this)
            .build()



        wallAdapter =
            object : FirebaseRecyclerAdapter<WallpaperItem, WallpaperViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): WallpaperViewHolder {

                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.staggered_recycler_item, parent, false)

//                    val height = parent.measuredHeight / 2
//                    itemView.minimumHeight = height
                    return WallpaperViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: WallpaperViewHolder,
                    position: Int,
                    model: WallpaperItem
                ) {

                    Log.d("imageurl", "imageURL is " + imageurlTest)


                    Picasso.get()
                        .load(imageurlTest)
                        //.placeholder(R.drawable.clean)
                        .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.itemView.stag_item_image, object : Callback {
                            override fun onSuccess() {
                                placeholder_anim_fav.visibility = View.GONE

                            }

                            override fun onError(e: Exception?) {
                                //try again if cache failed

                                Picasso.get()
                                    .load(imageurlTest)
                                    .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                                    .centerCrop()
                                    .error(R.drawable.ic_terrain_black_24dp)
                                    .into(holder.itemView.stag_item_image, object : Callback {
                                        override fun onSuccess() {
                                            placeholder_anim_fav.visibility = View.GONE

                                        }

                                        override fun onError(e: java.lang.Exception?) {
                                            Log.e("Error_Shaggz", "failed to fetch image")
                                        }

                                    })
                            }

                        })

                    holder.itemView.setOnClickListener {
                        Common.categoryIDSelected = wallAdapter.getRef(position).key!!
                        Common.wallName = model.wallpaperName
                        Common.selectBackground = model
                        Common.wallDescription = model.wallDesc
                        Common.selectBackgroundKey = wallAdapter.getRef(position).key!!
                        Common.viewcount = model.viewCount
                        Common.isFav = model.favourite
                        Common.selectBackground!!.imageURL = imageurlTest
                        startActivity<ViewWallpaperActivity>()
                    }
                }

            }
        wallAdapter.startListening()
        recyclerView.adapter = wallAdapter

    }

    override fun onStart() {
        super.onStart()
        wallAdapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        wallAdapter.startListening()

    }

    override fun onStop() {
        wallAdapter.stopListening()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}
