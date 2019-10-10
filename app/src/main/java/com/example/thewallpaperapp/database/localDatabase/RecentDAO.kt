package com.example.thewallpaperapp.database.localDatabase

import androidx.room.*
import com.example.thewallpaperapp.database.Recent
import io.reactivex.Flowable

@Dao
abstract class RecentDAO {
    @Query("SELECT * FROM recent ORDER BY saveTime DESC LIMIT 10")

    abstract fun getAllRecent(): Flowable<List<Recent>>

    @Insert
    abstract fun insertRecent(recent: Recent)

    @Update
    abstract fun updateRecent(recent: Recent)

    @Delete
    abstract fun deleteRecent(recent: Recent)

    @Query("DELETE FROM recent")
    abstract fun deleteAllRecent()
}