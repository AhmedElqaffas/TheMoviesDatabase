package com.example.moviesretrofit.mediaDetails

import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.database.AppDatabase

object CreditsDatabaseHandler {

    fun getMovieCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getMovieCredits(id)
    }

    fun getSeriesCredits(database: AppDatabase, id: Int): List<Person> {
        return database.getCreditsDao().getSeriesCredits(id)
    }
}