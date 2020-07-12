package com.example.moviesretrofit.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataRepositories.PopularMoviesRepository
import com.example.moviesretrofit.dataRepositories.PopularSeriesRepository
import com.example.moviesretrofit.dataRepositories.RatedMoviesRepository
import com.example.moviesretrofit.dataRepositories.RatedSeriesRepository
import com.example.moviesretrofit.models.MultiMedia
import com.example.moviesretrofit.models.MultiMediaRepositoryResponse


class MainViewModel: ViewModel() {
    fun getPopularMovies(page: Int): LiveData<MultiMediaRepositoryResponse>{
        return PopularMoviesRepository.makePopularMoviesRequest(page)
    }

    fun getRatedMovies(page: Int): LiveData<MultiMediaRepositoryResponse>{
        return RatedMoviesRepository.makeRatedMoviesRequest(page)
    }

    fun getPopularSeries(page: Int): LiveData<MultiMediaRepositoryResponse>{
        return PopularSeriesRepository.makePopularSeriesRequest(page)
    }

    fun getRatedSeries(page: Int): LiveData<MultiMediaRepositoryResponse>{
        return RatedSeriesRepository.makeRatedSeriesRequest(page)
    }
}