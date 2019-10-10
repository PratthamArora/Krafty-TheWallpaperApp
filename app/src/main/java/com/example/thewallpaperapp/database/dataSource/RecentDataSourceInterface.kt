package com.example.thewallpaperapp.database.dataSource

import com.example.thewallpaperapp.database.Recent
import io.reactivex.Flowable

interface RecentDataSourceInterface {

    fun getAllRecent(): Flowable<List<Recent>>
    fun insertRecent(recent: Recent)
    fun updateRecent(recent: Recent)
    fun deleteRecent(recent: Recent)
    fun deleteAllRecent()
}