package com.example.thewallpaperapp.ui

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.thewallpaperapp.common.Common
import com.example.thewallpaperapp.database.Recent
import com.example.thewallpaperapp.database.dataSource.RecentRepository
import com.example.thewallpaperapp.database.localDatabase.LocalDatabase
import com.example.thewallpaperapp.database.localDatabase.RecentDataSource
import com.example.thewallpaperapp.model.WallpaperItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.krafty.android.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_view_wallpaper.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.toast
import java.io.IOException

class ViewWallpaperActivity : AppCompatActivity() {


    //Room database

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var recentRepository: RecentRepository


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Common.permissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val fileName: String = Common.wallName + ".jpeg"
                    toast("Download started")

                    //Download using download manager
                    val request =
                        DownloadManager.Request(Uri.parse(Common.selectBackground!!.imageURL))
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    request.setDescription("Krafty")
                    request.setTitle(Common.wallName)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalFilesDir(
                        this,
                        Environment.DIRECTORY_DOWNLOADS,
                        fileName
                    )

                    val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    manager.enqueue(request)

                    Log.i("load", Common.selectBackground!!.imageURL)

                } else
                    toast("Please allow permission to download wallpapers")

            }
        }
    }


    private val target = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val wallpaperManager = WallpaperManager.getInstance(applicationContext)
            try {
                wallpaperManager.setBitmap(bitmap)
                Snackbar.make(root_layout, "Wallpaper has been set", Snackbar.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_wallpaper)
        setSupportActionBar(view_walls_toolbar)



        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,    //NO STATUS BAR
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,         //NO SCREENSHOTS,SCREENRECORD
//            WindowManager.LayoutParams.FLAG_SECURE
//        )


        //Init Room db
        compositeDisposable = CompositeDisposable()
        val database: LocalDatabase = LocalDatabase.getInstance(this)
        recentRepository =
            RecentRepository.getInstance(RecentDataSource.getInstance(database.recentDao()))



        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        collapsing_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsing_toolbar.title = Common.wallName

        wallDescription.text = Common.wallDescription


        //loading wallpaper

        Picasso.get()
            .load(Common.selectBackground!!.imageURL)
            .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
            .centerCrop()
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(image_thumb, object : Callback {
                override fun onSuccess() {
                    placeholder_anim_View.visibility = View.GONE
                    //to check if wall is selected as fav
                    checkFav()
                    //ViewCount
                    increaseViewCount()

                    //ADD TO RECENT
                    addToRecent()

                }

                override fun onError(e: Exception?) {
                    //try again if cache failed

                    Picasso.get()
                        .load(Common.selectBackground!!.imageURL)
                        .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(image_thumb, object : Callback {
                            override fun onSuccess() {
                                placeholder_anim_View.visibility = View.GONE
                                //to check if wall is selected as fav
                                checkFav()

                                //ViewCount
                                increaseViewCount()

                                //ADD TO RECENT
                                addToRecent()


                            }

                            override fun onError(e: java.lang.Exception?) {
                                Log.e("Error_Shaggz", "failed to fetch image")
                            }

                        })
                }

            })


        //checkbox Favourite
        checkBox_fav.setOnCheckedChangeListener { buttonView, isChecked ->
            if (FirebaseAuth.getInstance().currentUser == null) {
                toast("Please Log in first")
                buttonView.isChecked = false
            }

            val dbfavs =
                FirebaseAuth.getInstance().currentUser?.uid?.let {
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(it).child("favourites")
                        .child(Common.categorySelected)
                }
            if (isChecked) {
                Common.selectBackground!!.favourite = true
                dbfavs?.child(Common.selectBackgroundKey)?.setValue(
                    WallpaperItem(
                        Common.selectBackground!!.categoryID,
                        Common.selectBackground!!.favourite,
                        Common.selectBackground!!.imageURL,
                        Common.selectBackground!!.viewCount,
                        Common.wallDescription,
                        Common.wallName


                    )
                )

            } else {
                Common.selectBackground!!.favourite = false
                dbfavs?.child(Common.selectBackgroundKey)?.setValue(null)

            }


        }





        floating_wallpaper.setOnClickListener {
            Picasso.get().load(Common.selectBackground?.imageURL)
                .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .centerCrop().into(target)

        }

        floating_download.setOnClickListener {
            //check Permission


            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Common.permissionRequestCode
                )

                val fileName: String = Common.wallName + ".jpeg"
                toast("Download Started")

                //Download using download manager

                val request =
                    DownloadManager.Request(Uri.parse(Common.selectBackground!!.imageURL))
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                request.setDescription("Krafty")

                request.setTitle(Common.wallName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )


                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)

            }

        }
    }


    private fun checkFav() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(it).child("favourites")
                .child(Common.categorySelected).child(Common.selectBackgroundKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild("favourite"))
                            checkBox_fav.isChecked = true
                    }
                })
        }
    }


    private fun increaseViewCount() {
        FirebaseDatabase.getInstance()
            .getReference("Wallpapers")
            .child(Common.selectBackgroundKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("viewCount")) {
                        val wallpaperItem = snapshot.getValue(WallpaperItem::class.java)
                        val count: Long = wallpaperItem!!.viewCount + 1

                        val updateView = HashMap<String, Long>()
                        updateView["viewCount"] = count


                        FirebaseDatabase.getInstance()
                            .getReference("Wallpapers")
                            .child(Common.selectBackgroundKey)
                            .updateChildren(updateView as Map<String, Any>)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                                toast("Cannot update ViewCount")
                            }


                    } else {   //if viewcount is not set default
                        val updateView = HashMap<String, Long>()
                        updateView["viewCount"] = 1.toLong()

                        FirebaseDatabase.getInstance()
                            .getReference("Wallpapers")
                            .child(Common.selectBackgroundKey)
                            .updateChildren(updateView as Map<String, Any>)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                                toast("Cannot update ViewCount")
                            }
                    }
                }
            })

    }


    private fun addToRecent() {

        val disposable = Observable.create(ObservableOnSubscribe<Any> { emitter ->
            val recent = Recent(
                Common.selectBackground!!.imageURL,
                Common.selectBackground!!.categoryID,
                Common.wallName,
                System.currentTimeMillis().toString(),
                Common.selectBackgroundKey
            )
            recentRepository.insertRecent(recent)
            emitter.onComplete()
        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { },
                { t -> Log.e("ERROR", t.message!!) }, { })
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        Picasso.get().cancelRequest(target)
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }


}