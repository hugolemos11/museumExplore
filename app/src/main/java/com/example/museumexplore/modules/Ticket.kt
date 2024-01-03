package com.example.museumexplore.modules

data class Ticket(
    var id: String,
    var title: String,
    var price: Double,
    var description: String,
    var pathToImage: String?,
    var imageUrl: String? = null
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Ticket {
            return Ticket(
                id,
                snapshot["title"] as String,
                snapshot["price"] as Double,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}