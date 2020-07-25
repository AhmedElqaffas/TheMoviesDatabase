package com.example.moviesretrofit.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataRepositories.PopularMoviesRepository
import com.example.moviesretrofit.dataRepositories.PopularSeriesRepository
import com.example.moviesretrofit.dataRepositories.RatedMoviesRepository
import com.example.moviesretrofit.dataRepositories.RatedSeriesRepository
import com.example.moviesretrofit.dataClasses.MultiMediaResponse


class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        PopularMoviesRepository.createDatabase(application)
    }

    fun getPopularMovies(page: Int): LiveData<MultiMediaResponse>{
        return PopularMoviesRepository.makePopularMoviesRequest(page)
    }

    fun getRatedMovies(page: Int): LiveData<MultiMediaResponse>{
        return RatedMoviesRepository.makeRatedMoviesRequest(page)
    }

    fun getPopularSeries(page: Int): LiveData<MultiMediaResponse>{
        return PopularSeriesRepository.makePopularSeriesRequest(page)
    }

    fun getRatedSeries(page: Int): LiveData<MultiMediaResponse>{
        return RatedSeriesRepository.makeRatedSeriesRequest(page)
    }
}