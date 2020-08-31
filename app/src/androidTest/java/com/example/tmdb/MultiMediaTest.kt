package com.example.tmdb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tmdb.dataClasses.Genre
import com.example.tmdb.dataClasses.Movie
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.database.MultimediaDao
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiMediaTest{

    private lateinit var multimediaDao: MultimediaDao
    private lateinit var database: AppDatabase

    @Before
    fun createDatabase(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        multimediaDao = database.getMultimediaDao()
    }

    @After
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun saveAndRetrieveSingleMovie(){
        // Create Movie
        val movie = Movie(
            "title", 15, 75064643, "posterLink", "coverLink",
            7.8f, "2008-08-12", "movie",
            "dummy overviwq", 99980559f, 50000000, 2000000000,
            listOf(Genre(1, "Adventure"), Genre(2, "Comedy")),
            false, extraDetailsObtained = true, userId = "local"
        )

        // Save Movie
        runBlocking { movie.saveInDatabase(database) }

        // Retrieve Movie
        val retrievedMovie = runBlocking { multimediaDao.getSingleMovie(15) }

        // Assert that the retrieved movie is the same one saved
        assertTrue(EqualsBuilder.reflectionEquals(movie,retrievedMovie))
    }
}