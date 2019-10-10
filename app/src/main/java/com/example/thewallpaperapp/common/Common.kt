package com.example.thewallpaperapp.common

import com.example.thewallpaperapp.model.WallpaperItem

class Common {
    companion object {

        const val signInReqCode = 1001
        var categoryIDSelected: String = ""
        var categorySelected: String = ""
        var wallName: String = ""
        var wallDescription: String = ""

        var selectBackground: WallpaperItem? = null

        var selectBackgroundKey: String = ""
        var isFav: Boolean = false

        const val permissionRequestCode = 123
        var viewcount: Long = 0


    }

}