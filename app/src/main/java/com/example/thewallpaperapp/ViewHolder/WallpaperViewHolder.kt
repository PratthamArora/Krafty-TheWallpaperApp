package com.example.thewallpaperapp.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.thewallpaperapp.interfaceImplement.ItemClickListener

class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    var itemClickListener: ItemClickListener? = null


    override fun onClick(v: View?) {
        v?.let { itemClickListener?.onClick(it, adapterPosition) }

    }
}

