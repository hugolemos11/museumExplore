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
data class Image(
    @PrimaryKey
    var id: String,
    var museumId: String,
    var pathToImage: String?,
    var imageType: String
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>, imageType: String): Image {
            return Image(
                id,
                snapshot["museumId"] as  String,
                snapshot["pathToImage"] as? String?,
                imageType
            )
        }

        suspend fun fetchMuseumImagesData(museumId: String): List<Image> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("imagesCollectionMuseum")
                    .whereEqualTo("museumId", museumId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val museumImagesList = ArrayList<Image>()
                        for (document in documents) {
                            museumImagesList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data,
                                    "museumImage"
                                )
                            )
                        }
                        continuation.resume(museumImagesList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        suspend fun fetchArtWorksImagesData(museumId: String): List<Image> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("artWorks")
                    .whereEqualTo("museumId", museumId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val artWorksImagesList = ArrayList<Image>()
                        for (document in documents) {
                            artWorksImagesList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data,
                                    "artWorkImage"
                                )
                            )
                        }
                        continuation.resume(artWorksImagesList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM image WHERE imageType = 'museumImage' AND  museumId = :museumId")
    fun getAllMuseumImages(museumId: String): LiveData<List<Image>>

    @Query("SELECT * FROM image WHERE imageType = 'artWorkImage' AND museumId = :museumId")
    fun getAllArtWorksImages(museumId: String): LiveData<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(image: Image)

    @Query("DELETE FROM Image")
    fun delete()
}