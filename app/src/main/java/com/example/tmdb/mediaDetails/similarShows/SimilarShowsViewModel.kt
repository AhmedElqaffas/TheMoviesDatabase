package com.example.tmdb.mediaDetails.similarShows

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.helpers.MultiMediaCaster

class SimilarShowsViewModel: ViewModel() {

    fun getSimilarShows(multiMedia: MultiMedia): LiveData<List<MultiMedia>>{
        return SimilarShowsRepository.getSimilarShows(MultiMediaCaster.castMultimedia(multiMedia))
    }
}