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

@Entity
data class Image(
    @PrimaryKey
    var id: String,
    var pathToImage: String?,
    var imageType: String
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>, imageType: String): Image {
            return Image(
                id,
                snapshot["pathToImage"] as? String?,
                imageType
            )
        }

        fun fetchMuseumImagesData(museumId: String, onCompletion: (List<Image>) -> Unit) {
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
                    onCompletion(museumImagesList)
                }
        }

        fun fetchArtWorksImagesData(museumId: String, onCompletion: (List<Image>) -> Unit) {
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
                    onCompletion(artWorksImagesList)
                }
        }
    }
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM image WHERE imageType = 'museumImage'")
    fun getAllMuseumImages(): LiveData<List<Image>>

    @Query("SELECT * FROM image WHERE imageType = 'artWorkImage'")
    fun getAllArtWorksImages(): LiveData<List<Image>>

    @Query("SELECT * FROM image WHERE id = :id")
    fun get(id: String): Image

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(image: Image)

    @Delete
    fun delete(image: Image)
}