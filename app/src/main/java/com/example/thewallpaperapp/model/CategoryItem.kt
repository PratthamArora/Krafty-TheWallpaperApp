package com.example.thewallpaperapp.model

class CategoryItem {

    var imageURL: String = ""
    var name: String = ""


    constructor()

    constructor(imageURL: String, name: String) {
        this.imageURL = imageURL
        this.name = name
    }


}
