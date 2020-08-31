package com.example.tmdb.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tmdb.dataClasses.Movie
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.dataClasses.Series

@Dao
interface MultimediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(moviesList: List<Movie>)

    @Query("SELECT * FROM movies WHERE movies.id = :id")
    suspend fun getSingleMovie(id: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleMovie(movies: Movie)

    @Query("SELECT * FROM movies ORDER BY rating DESC")
    suspend fun getTopRatedMovies(): List<Movie>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    suspend fun getPopularMovies(): List<Movie>

    @Query("DELETE FROM movies WHERE isFavorite = 0 or movies.userId != :userId")
    suspend fun deleteCachedMovies(userId: String)

    @Query("UPDATE movies Set isFavorite = :favorite , userId = :userId WHERE id = :id")
    suspend fun updateMovieFavorite(id: Int, favorite: Boolean, userId: String)

    @Query("SELECT * FROM  movies WHERE isFavorite = 1 and userId = :userId")
    fun getFavoriteMovies(userId: String): LiveData<List<MultiMedia>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeries(seriesList: List<Series>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleSeries(series: Series)

    @Query("SELECT * FROM series WHERE series.id = :id")
    suspend fun getSingleSeries(id: Int): Series

    @Query("SELECT * FROM series ORDER BY rating DESC")
    suspend fun getTopRatedSeries(): List<Series>

    @Query("SELECT * FROM series ORDER BY popularity DESC")
    suspend fun getPopularSeries(): List<Series>

    @Query("DELETE FROM series WHERE isFavorite = 0 or userId != :userId")
    suspend fun deleteCachedSeries(userId: String)

    @Query("UPDATE series Set isFavorite = :favorite , userId = :userId WHERE id = :id")
    suspend fun updateSeriesFavorite(id: Int, favorite: Boolean, userId: String)

    @Query("SELECT * FROM  series WHERE isFavorite = 1 and userId = :userId")
    fun getFavoriteSeries(userId: String): LiveData<List<MultiMedia>>
}