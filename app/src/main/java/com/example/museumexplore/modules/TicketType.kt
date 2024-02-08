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
data class TicketType(
    @PrimaryKey
    var id: String,
    var type: String,
    var price: Double,
    var description: String,
    var maxTobuy: Int,
    var museumId: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): TicketType {
            return TicketType(
                id,
                snapshot["type"] as String,
                (snapshot["price"] as Number).toDouble(),
                snapshot["description"] as String,
                (snapshot["maxToBuy"] as Number).toInt(),
                snapshot["museumId"] as String,
                snapshot["pathToImage"] as? String?
            )
        }

        suspend fun fetchTicketTypesData(museumId: String): List<TicketType> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("ticketTypes")
                    .whereEqualTo("museumId", museumId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val ticketTypesList = ArrayList<TicketType>()
                        for (document in documents) {
                            ticketTypesList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data
                                )
                            )
                        }
                        continuation.resume(ticketTypesList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }
}

@Dao
interface TicketTypeDao {
    @Query("SELECT * FROM ticketType WHERE museumId = :museumId")
    fun getAll(museumId: String): LiveData<List<TicketType>>

    @Query("SELECT * FROM ticketType WHERE id = :id")
    fun get(id : String) : TicketType

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(ticketType: TicketType)

    @Query("DELETE FROM TicketType")
    fun delete()
}