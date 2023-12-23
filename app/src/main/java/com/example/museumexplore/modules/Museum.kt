package com.example.museumexplore.modules

data class Museum (var id: String, var name: String, var description: String, var location: String, var rate: Int, var pathToImage: String?) {

    companion object{
        fun fromSnapshot(id : String, snapshot: Map<String,Any>) : Museum{
            return Museum(id,
                snapshot["name"] as String,
                snapshot["description"] as String,
                snapshot["location"] as String,
                (snapshot["rate"] as Long).toInt(),
                snapshot["pathToImage"] as? String?
            )
        }
    }
}