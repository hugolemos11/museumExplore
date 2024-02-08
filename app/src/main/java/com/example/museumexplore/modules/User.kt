package com.example.museumexplore.modules

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.showToast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Entity
data class User(
    @PrimaryKey
    var id: String,
    val username: String,
    val pathToImage: String?,
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): User {
            return User(
                id,
                snapshot["username"] as String,
                snapshot["pathToImage"] as? String?
            )
        }

        suspend fun fetchUserData(uid: String): User {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val user = documentSnapshot.data?.let {
                                fromSnapshot(
                                    documentSnapshot.id, it
                                )
                            }
                            if (user != null) {
                                continuation.resume(user)
                            } else {
                                continuation.resumeWithException(NullPointerException("User is null"))
                            }
                        } else {
                            continuation.resumeWithException(NoSuchElementException("User not found"))
                        }
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        fun updateUserData(uid: String, userUpdates: Map<String, String?>): Task<User> {
            val db = Firebase.firestore
            val userDocumentRef = db.collection("users").document(uid)

            return userDocumentRef.update(userUpdates)
                .continueWithTask { task ->
                    if (task.isSuccessful) {
                        // Fetch the updated user data after the update
                        userDocumentRef.get()
                            .continueWith { documentSnapshotTask ->
                                if (documentSnapshotTask.isSuccessful) {
                                    fromSnapshot(uid, documentSnapshotTask.result!!.data!!)
                                } else {
                                    throw documentSnapshotTask.exception!!
                                }
                            }
                    } else {
                        throw task.exception!!
                    }
                }
        }

        fun createUser(uid: String, username: String): Task<User> {
            val user = User(uid, username, "userImages/default_user.png")
            Log.e("teste", user.toString())
            val db = Firebase.firestore
            val userDocumentRef = db.collection("users").document(uid)

            return userDocumentRef.set(user)
                .continueWithTask { task ->
                    if (task.isSuccessful) {
                        // Fetch the updated user data after the update
                        userDocumentRef.get()
                            .continueWith { documentSnapshotTask ->
                                if (documentSnapshotTask.isSuccessful) {
                                    fromSnapshot(uid, documentSnapshotTask.result!!.data!!)
                                } else {
                                    throw documentSnapshotTask.exception!!
                                }
                            }
                    } else {
                        throw task.exception!!
                    }
                }
        }

        fun deleteUserFromFirestore(uid: String, context: Context) {
            val db = Firebase.firestore

            val appDatabase = AppDatabase.getInstance(context)

            appDatabase?.userDao()?.delete(uid)
            db.collection("users").document(uid)
                .delete()
                .addOnSuccessListener {
                    Log.d("TESTE", "DocumentSnapshot successfully deleted from Firestore!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document from Firestore", e)
                }
        }
    }
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun get(id: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: User)

    @Query("DELETE FROM user WHERE id = :uid")
    fun delete(uid: String)
}