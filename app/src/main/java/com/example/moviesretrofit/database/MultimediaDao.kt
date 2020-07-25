package com.example.moviesretrofit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Series

@Dao
interface MultimediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(moviesList: List<Movie>)

    @Query("SELECT * FROM movies ORDER BY rating DESC")
    fun getTopRatedMovies(): List<Movie>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getPopularMovies(): List<Movie>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeries(seriesList: List<Series>)

    @Query("SELECT * FROM series ORDER BY rating DESC")
    fun getTopRatedSeries(): List<Series>

    @Query("SELECT * FROM series ORDER BY popularity DESC")
    fun getPopularSeries(): List<Series>
}