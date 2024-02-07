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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Entity
data class Category(
    @PrimaryKey var id: String,
    var museumId: String,
    var descritpion: String
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Category {
            return Category(
                id, snapshot["museumId"] as String, snapshot["description"] as String
            )
        }

        suspend fun fetchCategoriesData(museumId: String): List<Category> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("categories").whereEqualTo("museumId", museumId).get()
                    .addOnSuccessListener { documents ->
                        val categoriesList = ArrayList<Category>()
                        for (document in documents) {
                            categoriesList.add(
                                fromSnapshot(
                                    document.id, document.data
                                )
                            )
                        }
                        continuation.resume(categoriesList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        suspend fun fetchCategoryData(categoryId: String): Category {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("categories")
                    .document(categoryId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val category = documentSnapshot.data?.let {
                                fromSnapshot(
                                    documentSnapshot.id, it
                                )
                            }
                            if (category != null) {
                                continuation.resume(category)
                            } else {
                                continuation.resumeWithException(NullPointerException("Category is null"))
                            }
                        } else {
                            continuation.resumeWithException(NoSuchElementException("Category not found"))
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
interface CategoryDao {
    @Query("SELECT * FROM category WHERE museumId = :museumId")
    fun getAll(museumId: String): LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE id = :id")
    fun get(id: String): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(category: Category)

    @Query("DELETE FROM Category")
    fun delete()
}