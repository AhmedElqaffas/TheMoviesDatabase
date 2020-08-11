package com.example.moviesretrofit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviesretrofit.dataClasses.*

@Database(entities = [Movie::class, Series::class,
    CreditsAndMoviesForeignKeyTable::class,
    CreditsAndSeriesForeignKeyTable::class,
    Person::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun getMultimediaDao(): MultimediaDao
    abstract fun getCreditsDao(): CreditsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE != null)
                return INSTANCE!!

            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "tmdb"
                ).build()
                return INSTANCE!!
            }
        }
    }
}