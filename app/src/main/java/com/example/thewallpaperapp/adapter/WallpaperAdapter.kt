package com.example.thewallpaperapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thewallpaperapp.ViewHolder.WallpaperViewHolder
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.model.WallpaperItem
import com.example.thewallpaperapp.ui.ViewWallpaperActivity
import com.krafty.android.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.staggered_recycler_item.view.*
import org.jetbrains.anko.displayMetrics

class WallpaperAdapter(
    val context: Context,
    val wallpaper: List<WallpaperItem>
) :
    RecyclerView.Adapter<WallpaperViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staggered_recycler_item, parent, false)

        return WallpaperViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return wallpaper.size
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {

        Picasso.get()
            .load(wallpaper[position].imageURL)
            .resize(
                context.displayMetrics.widthPixels,
                context.displayMetrics.heightPixels
            )
            .centerCrop()
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(holder.itemView.stag_item_image, object : Callback {
                override fun onSuccess() {


                }

                override fun onError(e: Exception?) {
                    //try again if cache failed

                    Picasso.get()
                        .load(wallpaper[position].imageURL)
                        .resize(
                            context.displayMetrics.widthPixels,
                            context.displayMetrics.heightPixels
                        )
                        .centerCrop()
                        .error(R.drawable.ic_terrain_black_24dp)
                        .into(holder.itemView.stag_item_image, object : Callback {
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


           // Common.categoryIDSelected = wallpaper[position].key!!
            Common.wallName = wallpaperItem.wallpaperName
            wallpaperItem.imageURL=wallpaper[position].imageURL
            Common.selectBackground = wallpaperItem
            Common.wallDescription = wallpaperItem.wallDesc
            //Common.selectBackgroundKey = wallpaper[position].key!!
            Common.viewcount = wallpaperItem.viewCount
            Common.isFav = wallpaperItem.favourite

            context.startActivity(intent)
        }
    }

}
