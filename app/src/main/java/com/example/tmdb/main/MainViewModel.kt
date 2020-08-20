package com.example.tmdb.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.main.dataRepositories.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        FavoritesRepository.createDatabase(application)
        PopularMoviesRepository.createDatabase(application)
        RatedMoviesRepository.createDatabase(application)
        PopularSeriesRepository.createDatabase(application)
        RatedSeriesRepository.createDatabase(application)
    }

    fun getFavoriteMovies(): LiveData<List<MultiMedia>>{
        return FavoritesRepository.getFavoriteMovies(getCurrentUser()!!)
    }

    fun getFavoriteSeries(): LiveData<List<MultiMedia>>{
        return FavoritesRepository.getFavoriteSeries(getCurrentUser()!!)
    }

    fun getPopularMovies(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return PopularMoviesRepository.makePopularMoviesRequest(firstRequest, getCurrentUser()!!)
    }

    fun getRatedMovies(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return RatedMoviesRepository.makeRatedMoviesRequest(firstRequest)
    }

    fun getPopularSeries(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return PopularSeriesRepository.makePopularSeriesRequest(firstRequest, getCurrentUser()!!)
    }

    fun getRatedSeries(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return RatedSeriesRepository.makeRatedSeriesRequest(firstRequest)
    }

    private fun getCurrentUser(): String? = getApplication<Application>().
    getSharedPreferences("user",0).getString("userId", "null")
}