package com.example.moviesretrofit.mediaDetails.credits

import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.database.AppDatabase

object CreditsDatabaseHandler {

    suspend fun getMovieCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getMovieCredits(id)
    }

    suspend fun getSeriesCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getSeriesCredits(id)
    }
}