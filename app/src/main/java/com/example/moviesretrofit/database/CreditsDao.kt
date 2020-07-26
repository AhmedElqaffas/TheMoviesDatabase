package com.example.moviesretrofit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesretrofit.dataClasses.CreditsMultimediaLinking
import com.example.moviesretrofit.dataClasses.Person

@Dao
interface CreditsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredits(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun linkMovieAndCredits( creditsAndMoviesForeignKeyTable: CreditsMultimediaLinking.CreditsAndMoviesForeignKeyTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun linkSeriesAndCredits( creditsAndSeriesForeignKeyTable: CreditsMultimediaLinking.CreditsAndSeriesForeignKeyTable)

    @Query("SELECT * From persons LEFT JOIN CreditsAndMoviesForeignKeyTable ON " +
            "persons.name = CreditsAndMoviesForeignKeyTable.credits WHERE  " +
            "CreditsAndMoviesForeignKeyTable.media = :id")
    fun getMovieCredits(id: Int): List<Person>


}