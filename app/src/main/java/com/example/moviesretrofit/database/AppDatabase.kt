package com.example.moviesretrofit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.Series

@Database(entities = [Movie::class, Series::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun getMultimediaDao(): MultimediaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE != null)
                return INSTANCE!!

            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "tmdb"
                ).allowMainThreadQueries().build()
                return INSTANCE!!
            }
        }
    }
}