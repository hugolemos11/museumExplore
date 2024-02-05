package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
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
data class Ticket(
    @PrimaryKey
    var id: String,
    var uid: String,
    var typeId: String,
    var museumId: String,
    var amount: Int,
    var purchaseDate: Date,
    var visitDate: Date,
    var pathToImage: String
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Ticket {
            return Ticket(
                id,
                snapshot["uid"] as String,
                snapshot["typeId"] as String,
                snapshot["museumId"] as String,
                (snapshot["amount"] as Long).toInt(),
                (snapshot["purchaseDate"] as Timestamp).toDate(),
                (snapshot["visitDate"] as Timestamp).toDate(),
                snapshot["pathToImage"] as String
            )
        }

        suspend fun fetchTicketsData(uid: String): List<Ticket> {
            return suspendCoroutine { continuation ->
                val db = Firebase.firestore
                db.collection("tickets")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        val ticketsList = ArrayList<Ticket>()
                        for (document in documents) {
                            ticketsList.add(
                                fromSnapshot(
                                    document.id,
                                    document.data
                                )
                            )
                        }
                        continuation.resume(ticketsList)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }

        fun addTicket(ticket: Ticket, callback: (Boolean) -> Unit) {
            val db = Firebase.firestore

            // need this to create a ticket without the id field
            val ticketData: MutableMap<String, Any> = mutableMapOf(
                "uid" to ticket.uid,
                "typeId" to ticket.typeId,
                "museumId" to ticket.museumId,
                "amount" to ticket.amount,
                "purchaseDate" to ticket.purchaseDate,
                "visitDate" to ticket.visitDate,
                "pathToImage" to ticket.pathToImage
            )

            db.collection("tickets")
                .add(ticketData)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }

    }
}

@Dao
interface TicketDao {
    @Query("SELECT * FROM ticket WHERE uid = :uid")
    fun getAll(uid: String): LiveData<List<Ticket>>

    @Query("SELECT * FROM ticket WHERE id = :id")
    fun get(id: String): Ticket

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(ticket: Ticket)
}