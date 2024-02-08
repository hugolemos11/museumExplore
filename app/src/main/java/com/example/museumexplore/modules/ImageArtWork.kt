package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Entity
data class ImageArtWork (
    @PrimaryKey
    var id: String,
    var artWorkId: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): ImageArtWork {
            return ImageArtWork(
                id,
                snapshot["artWorkId"] as  String,
                snapshot["pathToImage"] as? String?
            )
        }

        suspend fun fetchArtWorksImagesData(artWorkId: String): List<ImageArtWork> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("imagesCollectionArtWork")
                    .whereEqualTo("artWorkId", artWorkId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val artWorksImagesList = ArrayList<ImageArtWork>()
                        for (document in documents) {
                            artWorksImagesList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data,
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
interface ImageArtWorkDao {
    @Query("SELECT * FROM imageArtWork WHERE artWorkId = :artWorkId")
    fun getAll(artWorkId: String): LiveData<List<ImageArtWork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(imageArtWork: ImageArtWork)

    @Query("DELETE FROM ImageArtWork")
    fun delete()
}