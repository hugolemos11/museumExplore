package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Entity
data class Event(
    @PrimaryKey
    var id: String,
    var title: String,
    var description: String,
    var museumId: String,
    var startDate: Date,
    var finishDate: Date,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Event {
            return Event(
                id,
                snapshot["title"] as String,
                snapshot["description"] as String,
                snapshot["museumId"] as String,
                (snapshot["startDate"] as Timestamp).toDate(),
                (snapshot["finishDate"] as Timestamp).toDate(),
                snapshot["pathToImage"] as? String?
            )
        }

        suspend fun fetchEventsData(museumId: String): List<Event> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("events")
                    .whereEqualTo("museumId", museumId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val eventsList = ArrayList<Event>()
                        for (document in documents) {
                            eventsList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data
                                )
                            )
                        }
                        continuation.resume(eventsList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        suspend fun fetchEventData(eventId: String): Event {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val event = documentSnapshot.data?.let {
                                fromSnapshot(
                                    documentSnapshot.id,
                                    it
                                )
                            }
                            if (event != null) {
                                continuation.resume(event)
                            } else {
                                continuation.resumeWithException(NullPointerException("Event is null"))
                            }
                        } else {
                            continuation.resumeWithException(NoSuchElementException("Event not found"))
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
interface EventDao {
    @Query("SELECT * FROM event WHERE museumId = :museumId")
    fun getAll(museumId: String) : LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE id = :id")
    fun get(id : String) : Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(event: Event)

    @Query("DELETE FROM Event")
    fun delete()
}