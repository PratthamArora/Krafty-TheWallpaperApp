package com.example.thewallpaperapp.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thewallpaperapp.ViewHolder.CategoryViewHolder
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.model.CategoryItem
import com.example.thewallpaperapp.ui.ListWallpapersActivity
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
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.layout_category_item.view.*
import org.jetbrains.anko.displayMetrics


class CategoryFragment : Fragment() {

    companion object {
        private var instance: CategoryFragment? = null
        fun getInstance(): CategoryFragment? {
            if (instance == null)
                instance = CategoryFragment()
            return instance
        }
    }


    //Firebase

    private val categoryBackground by lazy {
        FirebaseDatabase.getInstance().reference.child("CategoryBackground")
    }

    lateinit var adapter: FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>

    //View
    private var recyclerView: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_category, container, false)

        recyclerView = myView.findViewById(R.id.recycler_category)

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.layoutManager = staggeredGridLayoutManager

        loadCategory()

        recyclerView?.adapter = adapter

        return myView
    }

    private fun loadCategory() {

        val options = FirebaseRecyclerOptions.Builder<CategoryItem>()
            .setQuery(categoryBackground, CategoryItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = object : FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_category_item, parent, false)
                return CategoryViewHolder(itemView)
            }

            override fun onBindViewHolder(
                holder: CategoryViewHolder,
                position: Int,
                model: CategoryItem
            ) {
                categoryBackground.child("CategoryBackground")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value == null)
                                load_category.visibility = View.GONE

                        }

                    })


                Picasso.get()
                    .load(model.imageURL)
                    .resize(
                        activity!!.displayMetrics.widthPixels,
                        activity!!.displayMetrics.heightPixels
                    )
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.itemView.category_image, object : Callback {
                        override fun onSuccess() {
                            Log.i("check", "onsucces called")
                            holder.itemView.placeholder_anim_categ.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            Picasso.get()
                                .load(model.imageURL)
                                .resize(
                                    activity!!.displayMetrics.widthPixels,
                                    activity!!.displayMetrics.heightPixels
                                )
                                .centerCrop()
                                .error(R.drawable.ic_terrain_black_24dp)
                                .into(holder.itemView.category_image, object : Callback {
                                    override fun onSuccess() {
                                        holder.itemView.placeholder_anim_categ.visibility =
                                            View.GONE
                                    }

                                    override fun onError(e: java.lang.Exception?) {
                                        Log.e("Error_image", "failed to fetch image")
                                    }

                                })
                        }

                    })


                holder.itemView.category_name.text = model.name

                holder.itemView.setOnClickListener {

                    Common.categoryIDSelected = adapter.getRef(position).key!!
                    Common.categorySelected = model.name
                    val intent = Intent(activity, ListWallpapersActivity::class.java)
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