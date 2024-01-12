package com.example.museumexplore.modules

data class ArtWorks(
    var id: String,
    var name: String,
    var artist: String,
    var year: Int,
    var category: String,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): ArtWorks {
            return ArtWorks(
                id,
                snapshot["name"] as String,
                snapshot["artist"] as String,
                (snapshot["year"] as Long).toInt(),
                snapshot["category"] as String,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}