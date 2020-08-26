package com.example.tmdb.mediaDetails.credits

import com.example.tmdb.dataClasses.Person
import com.example.tmdb.database.AppDatabase

object CreditsDatabaseHandler {

    suspend fun getMovieCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getMovieCredits(id)
    }

    suspend fun getSeriesCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getSeriesCredits(id)
    }
}