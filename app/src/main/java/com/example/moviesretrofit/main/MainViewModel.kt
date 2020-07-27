package com.example.moviesretrofit.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.main.dataRepositories.PopularMoviesRepository
import com.example.moviesretrofit.main.dataRepositories.PopularSeriesRepository
import com.example.moviesretrofit.main.dataRepositories.RatedMoviesRepository
import com.example.moviesretrofit.main.dataRepositories.RatedSeriesRepository
import com.example.moviesretrofit.dataClasses.MultiMediaResponse


class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        PopularMoviesRepository.createDatabase(application)
        RatedMoviesRepository.createDatabase(application)
        PopularSeriesRepository.createDatabase(application)
        RatedSeriesRepository.createDatabase(application)
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