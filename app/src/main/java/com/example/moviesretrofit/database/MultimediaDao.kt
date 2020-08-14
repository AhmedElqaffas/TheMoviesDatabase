package com.example.moviesretrofit.database

import androidx.room.*
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.Series

@Dao
interface MultimediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(moviesList: List<Movie>)

    @Query("SELECT * FROM movies WHERE movies.id = :id")
    suspend fun getSingleMovie(id: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleMovie(movies: Movie)

    @Query("SELECT * FROM movies ORDER BY rating DESC")
    suspend fun getTopRatedMovies(): List<Movie>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    suspend fun getPopularMovies(): List<Movie>

    @Query("DELETE FROM movies")
    suspend fun deleteCachedMovies()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(seriesList: List<Series>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleSeries(series: Series)

    @Query("SELECT * FROM series WHERE series.id = :id")
    suspend fun getSingleSeries(id: Int): Series

    @Query("SELECT * FROM series ORDER BY rating DESC")
    suspend fun getTopRatedSeries(): List<Series>

    @Query("SELECT * FROM series ORDER BY popularity DESC")
    suspend fun getPopularSeries(): List<Series>

    @Query("DELETE FROM series")
    suspend fun deleteCachedSeries()
}