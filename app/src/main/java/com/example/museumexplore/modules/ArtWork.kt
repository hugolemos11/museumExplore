package com.example.museumexplore.modules

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.museumexplore.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Entity
data class ArtWork(
    @PrimaryKey
    var id: String,
    var name: String,
    var artist: String,
    var year: Int,
    var categoryId: String,
    var description: String,
    var museumId: String,
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
                snapshot["museumId"] as String,
                snapshot["pathToImage"] as? String?
            )
        }

        suspend fun fetchArtWorksData(museumId: String): List<ArtWork> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("artWorks")
                    .whereEqualTo("museumId", museumId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val artWorksList = ArrayList<ArtWork>()
                        for (document in documents) {
                            artWorksList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data
                                )
                            )
                        }
                        continuation.resume(artWorksList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        suspend fun fetchArtWorkData(artWorkId: String): ArtWork {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("artWorks")
                    .document(artWorkId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val artWork = documentSnapshot.data?.let {
                                fromSnapshot(
                                    documentSnapshot.id,
                                    it
                                )
                            }
                            if (artWork != null) {
                                continuation.resume(artWork)
                            } else {
                                continuation.resumeWithException(NullPointerException("ArtWork is null"))
                            }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }
}

@Dao
interface ArtWorkDao {
    @Query("SELECT * FROM artWork WHERE museumId = :museumId")
    fun getAll(museumId: String) : LiveData<List<ArtWork>>

    @Query("SELECT * FROM artWork WHERE id = :id")
    fun get(id : String) : ArtWork

    @Query("SELECT * FROM artWork WHERE museumId = :museumId AND categoryId = :categoryId")
    fun filterByCategory(museumId: String, categoryId: String): LiveData<List<ArtWork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(artWork: ArtWork)

    @Query("DELETE FROM ArtWork")
    fun delete()
}