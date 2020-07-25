package com.example.moviesretrofit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia

@Dao
interface MultimediaDao {
    /*@Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(multiMedia: List<MultiMedia>)
*/
    @Query("SELECT * FROM movies ORDER BY rating DESC")
    fun getTopRatedMovies(): List<Movie>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getPopularMovies(): List<Movie>
}