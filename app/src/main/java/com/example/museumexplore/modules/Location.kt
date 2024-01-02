package com.example.museumexplore.modules

data class Location (
    var id : String,
    var longitude : Double,
    var latitude : Double
) {
    companion object{
        fun fromSnapshot(id : String, snapshot: Map<String,Any>) : Location{
            return Location(id,
                snapshot["longitude"] as Double,
                snapshot["latitude"] as Double
            )
        }
    }
}