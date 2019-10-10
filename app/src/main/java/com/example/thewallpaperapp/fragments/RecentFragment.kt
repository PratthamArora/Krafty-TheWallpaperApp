package com.example.thewallpaperapp.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thewallpaperapp.adapter.RecyclerAdapter
import com.example.thewallpaperapp.database.Recent
import com.example.thewallpaperapp.database.dataSource.RecentRepository
import com.example.thewallpaperapp.database.localDatabase.LocalDatabase
import com.example.thewallpaperapp.database.localDatabase.RecentDataSource
import com.krafty.android.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecentFragment : Fragment() {


    companion object {
        private var instance: RecentFragment? = null
        fun getInstance(): RecentFragment? {
            if (instance == null)
                instance = RecentFragment()
            return instance
        }
    }

    private var recyclerView: RecyclerView? = null
    private lateinit var myAdapter: RecyclerAdapter
    private lateinit var recentList: ArrayList<Recent>

    //Room database

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var recentRepository: RecentRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_recents, container, false)


        //Init Room db
        compositeDisposable = CompositeDisposable()
        val database: LocalDatabase = context?.let { LocalDatabase.getInstance(it) }!!
        recentRepository =
            RecentRepository.getInstance(RecentDataSource.getInstance(database.recentDao()))


        recyclerView = myView.findViewById(R.id.recycler_recents)
        recyclerView?.setHasFixedSize(true)

//        val gridLayoutManager = GridLayoutManager(context, 2)
//        recyclerView?.layoutManager = gridLayoutManager

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.layoutManager = staggeredGridLayoutManager

        recentList = ArrayList()


        myAdapter = context?.let { RecyclerAdapter(it, recentList) }!!

        recyclerView?.adapter = myAdapter


        loadRecents()
        return myView
    }

    private fun loadRecents() {
        val disposable: Disposable =
            recentRepository.getAllRecent().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { recent -> onGetAllRecentSuccess(recent) },
                    { t -> Log.e("ERROR", t?.message) })
        compositeDisposable.add(disposable)
    }

    private fun onGetAllRecentSuccess(recent: List<Recent>?) {

        recentList.clear()
        recent?.let { recentList.addAll(it) }
        myAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
