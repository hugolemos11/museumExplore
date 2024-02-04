package com.example.museumexplore.modules

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Ticket(
    @PrimaryKey
    var id: String,
    var type: String,
    var price: Double,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Ticket {
            return Ticket(
                id,
                snapshot["type"] as String,
                snapshot["price"] as Double,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}

@Dao
interface TicketDao {
    @Query("SELECT * FROM ticket")
    fun getAll() : LiveData<List<Ticket>>

    @Query("SELECT * FROM ticket WHERE id = :id")
    fun get(id : String) : Ticket

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(ticket: Ticket)
}