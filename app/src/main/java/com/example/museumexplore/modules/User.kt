package com.example.museumexplore.modules

data class User (
    val username: String,
    val phoneNumber: Int?,
    val password: String,
    val pathToImage: String?,
) {
    companion object {
        fun fromSnapshot(snapshot: Map<String, Any>): User {
            return User(
                snapshot["username"] as String,
                (snapshot["phoneNumber"] as? Long)?.toInt(),
                snapshot["password"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}