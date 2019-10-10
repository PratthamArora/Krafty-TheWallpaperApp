package com.example.thewallpaperapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thewallpaperapp.ViewHolder.WallpaperViewHolder
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.database.Recent
import com.example.thewallpaperapp.model.WallpaperItem
import com.example.thewallpaperapp.ui.ViewWallpaperActivity
import com.krafty.android.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_recent.view.*
import org.jetbrains.anko.displayMetrics

class RecyclerAdapter(
    val context: Context,
    val recent: List<Recent>
) :
    RecyclerView.Adapter<WallpaperViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recent, parent, false)

        return WallpaperViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return recent.size
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {

        Picasso.get()
            .load(recent[position].imageURL)
            .resize(
                context.displayMetrics.widthPixels,
                context.displayMetrics.heightPixels
            )
            .centerCrop()
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(holder.itemView.recent_image, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    //try again if cache failed

                    Picasso.get()
                        .load(recent[position].imageURL)
                        .resize(
                            context.displayMetrics.widthPixels,
                            context.displayMetrics.heightPixels
                        )
                        .centerCrop()
                        .error(R.drawable.ic_terrain_black_24dp)
                        .into(holder.itemView.recent_image, object : Callback {
                            override fun onSuccess() {
                            }

                            override fun onError(e: java.lang.Exception?) {
                                Log.e("Error_Shaggz", "failed to fetch image")
                            }

                        })
                }

            })


        holder.itemView.setOnClickListener {

            val intent = Intent(context, ViewWallpaperActivity::class.java)

            val wallpaperItem = WallpaperItem()
            wallpaperItem.categoryID = recent[position].categoryID
            wallpaperItem.imageURL = recent[position].imageURL
            wallpaperItem.wallpaperName = recent[position].wallpaperName
            Common.wallName = wallpaperItem.wallpaperName
            Common.selectBackground = wallpaperItem
            Common.selectBackgroundKey = recent[position].key
            context.startActivity(intent)
        }
    }

}
