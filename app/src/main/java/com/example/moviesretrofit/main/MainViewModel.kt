package com.example.moviesretrofit.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import com.example.moviesretrofit.main.dataRepositories.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        FavoritesRepository.createDatabase(application)
        PopularMoviesRepository.createDatabase(application)
        RatedMoviesRepository.createDatabase(application)
        PopularSeriesRepository.createDatabase(application)
        RatedSeriesRepository.createDatabase(application)
    }

    fun getFavoriteMovies(): LiveData<List<MultiMedia>>{
        return FavoritesRepository.getFavoriteMovies()
    }

    fun getFavoriteSeries(): LiveData<List<MultiMedia>>{
        return FavoritesRepository.getFavoriteSeries()
    }

    fun getPopularMovies(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return PopularMoviesRepository.makePopularMoviesRequest(firstRequest)
    }

    fun getRatedMovies(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return RatedMoviesRepository.makeRatedMoviesRequest(firstRequest)
    }

    fun getPopularSeries(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return PopularSeriesRepository.makePopularSeriesRequest(firstRequest)
    }

    fun getRatedSeries(firstRequest: Boolean): LiveData<List<MultiMedia>>{
        return RatedSeriesRepository.makeRatedSeriesRequest(firstRequest)
    }
}