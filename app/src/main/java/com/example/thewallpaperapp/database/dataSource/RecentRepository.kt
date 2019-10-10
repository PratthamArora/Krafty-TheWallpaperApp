package com.example.thewallpaperapp.database.dataSource

import com.example.thewallpaperapp.database.Recent
import io.reactivex.Flowable

class RecentRepository(private var mLocalDataSource: RecentDataSourceInterface) :
    RecentDataSourceInterface {

    companion object {
        private var instance: RecentRepository? = null

        fun getInstance(mLocalDataSource: RecentDataSourceInterface): RecentRepository {
            if (instance == null) {
                instance = RecentRepository(mLocalDataSource)
            }
            return instance as RecentRepository
        }

    }


    override fun getAllRecent(): Flowable<List<Recent>> {
        return mLocalDataSource.getAllRecent()
    }

    override fun insertRecent(recent: Recent) {
        return mLocalDataSource.insertRecent(recent)
    }

    override fun updateRecent(recent: Recent) {
        return mLocalDataSource.updateRecent(recent)
    }

    override fun deleteRecent(recent: Recent) {
        return mLocalDataSource.deleteRecent(recent)
    }

    override fun deleteAllRecent() {
        return mLocalDataSource.deleteAllRecent()
    }
}