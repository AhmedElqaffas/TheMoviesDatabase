package com.example.moviesretrofit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesretrofit.dataClasses.CreditsAndMoviesForeignKeyTable
import com.example.moviesretrofit.dataClasses.CreditsAndSeriesForeignKeyTable
import com.example.moviesretrofit.dataClasses.Person

@Dao
interface CreditsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredits(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun linkMovieAndCredits( creditsAndMoviesForeignKeyTable: CreditsAndMoviesForeignKeyTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun linkSeriesAndCredits( creditsAndSeriesForeignKeyTable: CreditsAndSeriesForeignKeyTable)

    @Query("SELECT * From persons LEFT JOIN CreditsAndMoviesForeignKeyTable ON " +
            "persons.name = CreditsAndMoviesForeignKeyTable.credits WHERE  " +
            "CreditsAndMoviesForeignKeyTable.media = :id")
    fun getMovieCredits(id: Int): List<Person>

    @Query("SELECT * From persons LEFT JOIN CreditsAndSeriesForeignKeyTable ON " +
            "persons.name = CreditsAndSeriesForeignKeyTable.credits WHERE  " +
            "CreditsAndSeriesForeignKeyTable.media = :id")
    fun getSeriesCredits(id: Int): List<Person>

}