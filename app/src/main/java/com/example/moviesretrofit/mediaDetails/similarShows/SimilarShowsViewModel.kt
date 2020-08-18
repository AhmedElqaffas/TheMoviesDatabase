package com.example.moviesretrofit.mediaDetails.similarShows

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.helpers.MultiMediaCaster

class SimilarShowsViewModel: ViewModel() {

    fun getSimilarShows(multiMedia: MultiMedia): LiveData<List<MultiMedia>>{
        return SimilarShowsRepository.getSimilarShows(MultiMediaCaster.castMultimedia(multiMedia))
    }
}