package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Entity
data class ArtWork(
    @PrimaryKey
    var id: String,
    var name: String,
    var artist: String,
    var year: Int,
    var categoryId: String,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): ArtWork {
            return ArtWork(
                id,
                snapshot["name"] as String,
                snapshot["artist"] as String,
                (snapshot["year"] as Long).toInt(),
                snapshot["categoryId"] as String,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }

//    private val db = Firebase.firestore

//    private fun fetchArtWorkData(artWorkId: String) {
//        db.collection("artWorks")
//            .document(artWorkId)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//
//                if (documentSnapshot.exists()) {
//                    artWork =
//                        documentSnapshot.data?.let {
//                            ArtWorks.fromSnapshot(
//                                documentSnapshot.id,
//                                it
//                            )
//                        }
//                } else {
//                    showToast("Artwork with id $id not found", requireContext())
//                }
//            }
//            .addOnFailureListener {
//                showToast("An error occurred: ${it.localizedMessage}", requireContext())
//            }
//    }
}

@Dao
interface ArtWorkDao {
    @Query("SELECT * FROM artWork")
    fun getAll() : LiveData<List<ArtWork>>

    @Query("SELECT * FROM artWork WHERE id = :id")
    fun get(id : String) : ArtWork

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(artWork: ArtWork)

    @Delete
    fun  delete(artWork: ArtWork)
}