package com.example.museumexplore.modules

data class Event(
    var id: String,
    var title: String,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Event {
            return Event(
                id,
                snapshot["title"] as String,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}