package com.example.moviesretrofit.database

import androidx.room.*
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.Series

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

    @Query("DELETE FROM movies WHERE isFavorite = 0")
    suspend fun deleteCachedMovies()

    @Query("UPDATE movies Set isFavorite = :favorite WHERE id = :id")
    suspend fun updateMovieFavoriteField(id: Int, favorite: Boolean)

    @Query("SELECT isFavorite FROM  movies WHERE id = :id")
    suspend fun getMovieIsFavorite(id: Int): Boolean


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

    @Query("DELETE FROM series WHERE isFavorite = 0")
    suspend fun deleteCachedSeries()

    @Query("UPDATE series Set isFavorite = :favorite WHERE id = :id")
    suspend fun updateSeriesFavoriteField(id: Int, favorite: Boolean)

    @Query("SELECT isFavorite FROM series WHERE id = :id")
    suspend fun getSeriesIsFavorite(id: Int): Boolean
}