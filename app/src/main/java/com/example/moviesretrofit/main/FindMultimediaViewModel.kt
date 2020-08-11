package com.example.moviesretrofit.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.main.dataRepositories.FindMultiMediaRepository

class FindMultimediaViewModel: ViewModel() {
    fun findMediaByName(name: String, searchTextChanged: Boolean): LiveData<List<MultiMedia>>{
        return FindMultiMediaRepository.findMediaByName(name, searchTextChanged)
    }
}