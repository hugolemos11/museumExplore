package com.example.museumexplore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.modules.ArtWorkDao
import com.example.museumexplore.modules.Category
import com.example.museumexplore.modules.CategoryDao
import com.example.museumexplore.modules.Event
import com.example.museumexplore.modules.EventDao
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageArtWork
import com.example.museumexplore.modules.ImageArtWorkDao
import com.example.museumexplore.modules.ImageDao
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.modules.MuseumDao
import com.example.museumexplore.modules.Ticket
import com.example.museumexplore.modules.TicketDao
import com.example.museumexplore.modules.TicketType
import com.example.museumexplore.modules.TicketTypeDao
import com.example.museumexplore.modules.User
import com.example.museumexplore.modules.UserDao

@Database(entities = [Museum::class, ArtWork::class, TicketType::class, Event::class, Image::class, ImageArtWork::class, Category::class, User::class, Ticket::class], version = 9)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun museumDao() : MuseumDao
    abstract fun artWorkDao() : ArtWorkDao
    abstract fun ticketTypesDao() : TicketTypeDao
    abstract fun eventDao() : EventDao
    abstract fun imageDao() : ImageDao
    abstract fun imageArtWorkDao() : ImageArtWorkDao
    abstract fun categoryDao() : CategoryDao
    abstract fun userDao() : UserDao
    abstract fun ticketDao() : TicketDao

    companion object{

        @Volatile
        private var INSTANCE : AppDatabase? =  null

        fun getInstance(context: Context) : AppDatabase? {

            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java, "db-museum_explore"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }
    }

}