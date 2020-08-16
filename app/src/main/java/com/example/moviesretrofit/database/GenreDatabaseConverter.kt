package com.example.moviesretrofit.database

import androidx.room.TypeConverter
import com.example.moviesretrofit.dataClasses.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GenreDatabaseConverter {
    @TypeConverter
    @JvmStatic
    fun fromGenre(value: List<Genre>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    @JvmStatic
    fun toGenre(value: String): List<Genre> {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>(){}.type
        val genresList: List<Genre> = gson.fromJson(value, type)
        return genresList
    }
}