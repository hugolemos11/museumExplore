package com.example.museumexplore.modules

data class Ticket(
    var id: String,
    var type: String,
    var price: Double,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Ticket {
            return Ticket(
                id,
                snapshot["type"] as String,
                snapshot["price"] as Double,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}