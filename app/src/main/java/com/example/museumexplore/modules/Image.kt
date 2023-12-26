package com.example.museumexplore.modules

data class Image (
    var id: String,
    var pathToImage: String?
){
    companion object{
        fun fromSnapshot(id : String, snapshot: Map<String,Any>) : Image{
            return Image(id,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}