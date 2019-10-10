package com.example.thewallpaperapp.model

import com.google.firebase.database.Exclude

class WallpaperItem {

//    @Exclude
//    var key: String = ""
    var categoryID: String = ""
    @Exclude
    var favourite: Boolean = false

    var imageURL: String = ""
    var viewCount: Long = 0
    var wallDesc: String = ""
    var wallpaperName: String = ""


    constructor()
    constructor(
        categoryID: String,
        favourite: Boolean,
        imageURL: String,
        viewCount: Long,
        wallDesc: String,
        wallpaperName: String
    ) {
        this.categoryID = categoryID
        this.favourite = favourite
        this.imageURL = imageURL
        this.viewCount = viewCount
        this.wallDesc = wallDesc
        this.wallpaperName = wallpaperName
    }


}