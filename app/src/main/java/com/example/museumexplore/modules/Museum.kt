package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.museumexplore.showToast
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Entity
data class Museum(
    @PrimaryKey
    var id: String,
    var name: String,
    var description: String,
    var location: Map<String, Double>,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Museum {
            return Museum(
                id,
                snapshot["name"] as? String ?: "",
                snapshot["description"] as? String ?: "",
                snapshot["location"] as? Map<String, Double> ?: emptyMap(),
                snapshot["pathToImage"] as? String?
            )
        }



        suspend fun fetchMuseumsData(): List<Museum> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("museums")
                    .get()
                    .addOnSuccessListener { documents ->
                        val museumsList = ArrayList<Museum>()

                        for (document in documents) {
                            museumsList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data
                                )
                            )
                        }
                        continuation.resume(museumsList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }
}

@Dao
interface MuseumDao {
    @Query("SELECT * FROM museum")
    fun getAll(): LiveData<List<Museum>>

    @Query("SELECT * FROM museum WHERE id = :id")
    fun get(id: String): Museum

    @Query("SELECT * FROM museum WHERE name LIKE '%' || :name || '%'")
    fun getFilteredByName(name: String?): LiveData<List<Museum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(museum: Museum)

    @Query("DELETE FROM Museum")
    fun delete()
}