package com.example.thewallpaperapp.database.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thewallpaperapp.database.Recent

@Database(entities = [Recent::class], version = 2)
abstract class LocalDatabase : RoomDatabase() {


    abstract fun recentDao(): RecentDAO


    companion object {

        fun getInstance(context: Context): LocalDatabase {
            val databaseName = "TheWallpaperApp"

            var instance: LocalDatabase? = null

            if (instance == null) {
                instance = Room.databaseBuilder(context, LocalDatabase::class.java, databaseName)
                    .fallbackToDestructiveMigration().build()
            }
            return instance

        }
    }

}


