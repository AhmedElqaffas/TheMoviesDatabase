package com.example.tmdb.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.main.dataRepositories.FindMultiMediaRepository

class FindMultimediaViewModel: ViewModel() {
    fun findMediaByName(name: String, searchTextChanged: Boolean): LiveData<List<MultiMedia>>{
        return FindMultiMediaRepository.findMediaByName(name, searchTextChanged)
    }
}