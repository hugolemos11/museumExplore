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
import java.util.Date

@Entity
data class Ticket(
    @PrimaryKey
    var id: String,
    var uid: String,
    var typeId: String,
    var museumId: String,
    var amount: Int,
    var purchaseDate: Date,
    var visitDate: Date
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Ticket {
            return Ticket(
                id,
                snapshot["uid"] as String,
                snapshot["typeId"] as String,
                snapshot["museumId"] as String,
                snapshot["amount"] as Int,
                snapshot["purchaseDate"] as Date,
                snapshot["visitDate"] as Date
            )
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
                "visitDate" to ticket.visitDate
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