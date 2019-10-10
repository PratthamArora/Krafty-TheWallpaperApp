package com.example.thewallpaperapp.database.localDatabase

import com.example.thewallpaperapp.database.Recent
import com.example.thewallpaperapp.database.dataSource.RecentDataSourceInterface
import io.reactivex.Flowable

class RecentDataSource(private var recentDAO: RecentDAO) : RecentDataSourceInterface {

    companion object {
        var instance: RecentDataSource? = null

        fun getInstance(recentDAO: RecentDAO): RecentDataSource {
            if (instance == null) {
                instance = RecentDataSource(recentDAO)
            }
            return instance as RecentDataSource
        }

    }


    override fun getAllRecent(): Flowable<List<Recent>> {
        return recentDAO.getAllRecent()
    }

    override fun insertRecent(recent: Recent) {
        return recentDAO.insertRecent(recent)
    }

    override fun updateRecent(recent: Recent) {
        return recentDAO.updateRecent(recent)
    }

    override fun deleteRecent(recent: Recent) {
        return recentDAO.deleteRecent(recent)
    }

    override fun deleteAllRecent() {
        return recentDAO.deleteAllRecent()
    }
}