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
data class Event(
    @PrimaryKey
    var id: String,
    var title: String,
    var description: String,
    var pathToImage: String?
) {
    companion object {
        fun fromSnapshot(id: String, snapshot: Map<String, Any>): Event {
            return Event(
                id,
                snapshot["title"] as String,
                snapshot["description"] as String,
                snapshot["pathToImage"] as? String?
            )
        }
    }
}

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll() : LiveData<List<Event>>

    @Query("SELECT * FROM event WHERE id = :id")
    fun get(id : String) : Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(event: Event)

    @Delete
    fun  delete(event: Event)
}