package com.example.museumexplore.modules

data class Museum(
    var id: String,
    var name: String,
    var nameSearch: String,
    var description: String,
    var rate: Int,
    var latitude: Double,
    var longitude: Double,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Museum {
            val location = snapshot["location"] as? Map<*, *>

            return if (location != null) {
                Museum(
                    id,
                    snapshot["name"] as String,
                    snapshot["nameSearch"] as String,
                    snapshot["description"] as String,
                    (snapshot["rate"] as Long).toInt(),
                    location["latitude"] as Double,
                    location["longitude"] as Double,
                    snapshot["pathToImage"] as? String?
                )
            } else {
                Museum(
                    id,
                    snapshot["name"] as String,
                    snapshot["nameSearch"] as String,
                    snapshot["description"] as String,
                    (snapshot["rate"] as Long).toInt(),
                    0.0,
                    0.0,
                    snapshot["pathToImage"] as? String?
                )
            }
        }
    }
}