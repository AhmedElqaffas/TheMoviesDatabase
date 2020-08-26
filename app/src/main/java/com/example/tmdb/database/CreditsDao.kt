package com.example.tmdb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tmdb.dataClasses.CreditsAndMoviesForeignKeyTable
import com.example.tmdb.dataClasses.CreditsAndSeriesForeignKeyTable
import com.example.tmdb.dataClasses.Person

@Dao
interface CreditsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredits(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkMovieAndCredits( creditsAndMoviesForeignKeyTable: CreditsAndMoviesForeignKeyTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkSeriesAndCredits( creditsAndSeriesForeignKeyTable: CreditsAndSeriesForeignKeyTable)

    @Query("SELECT * From persons LEFT JOIN CreditsAndMoviesForeignKeyTable ON " +
            "persons.name = CreditsAndMoviesForeignKeyTable.credits WHERE  " +
            "CreditsAndMoviesForeignKeyTable.media = :id")
    suspend fun getMovieCredits(id: Int): List<Person>

    @Query("SELECT * From persons LEFT JOIN CreditsAndSeriesForeignKeyTable ON " +
            "persons.name = CreditsAndSeriesForeignKeyTable.credits WHERE  " +
            "CreditsAndSeriesForeignKeyTable.media = :id")
    suspend fun getSeriesCredits(id: Int): List<Person>

}