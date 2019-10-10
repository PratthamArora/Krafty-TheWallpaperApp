package com.example.thewallpaperapp.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "Recent", primaryKeys = ["imageURL", "categoryID", "wallpaperName"])
class Recent {

    @ColumnInfo(name = "imageURL")
    @NonNull
    var imageURL: String = ""

    @ColumnInfo(name = "categoryID")
    @NonNull
    var categoryID: String = ""

    @ColumnInfo(name = "wallpaperName")
    @NonNull
    var wallpaperName: String = ""

    @ColumnInfo(name = "saveTime")
    var saveTime: String = ""

    @ColumnInfo(name = "key")
    var key: String = ""

    constructor(
        imageURL: String,
        categoryID: String,
        wallpaperName: String,
        saveTime: String,
        key: String
    ) {
        this.imageURL = imageURL
        this.categoryID = categoryID
        this.wallpaperName = wallpaperName
        this.saveTime = saveTime
        this.key = key
    }
}