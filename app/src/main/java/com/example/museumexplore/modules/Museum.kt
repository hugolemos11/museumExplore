package com.example.museumexplore.modules

import android.util.Log
import com.google.firebase.firestore.GeoPoint

data class Museum(
    var id: String,
    var name: String,
    var nameSearch: String,
    var description: String,
    var rate: Int,
    var location: Map<String, Double>,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Museum {
            return Museum(
                id,
                snapshot["name"] as String,
                snapshot["nameSearch"] as String,
                snapshot["description"] as String,
                (snapshot["rate"] as Long).toInt(),
                snapshot["location"] as Map<String, Double>,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}