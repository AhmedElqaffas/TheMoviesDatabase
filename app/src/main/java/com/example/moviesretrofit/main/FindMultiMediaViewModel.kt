package com.example.moviesretrofit.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataRepositories.FindMultiMediaRepository
import com.example.moviesretrofit.dataClasses.MultiMediaResponse

class FindMultiMediaViewModel: ViewModel() {
    fun findMediaByName(page: Int, name: String, searchTextChanged: Boolean): LiveData<MultiMediaResponse>{
        return FindMultiMediaRepository.findMediaByName(page, name, searchTextChanged)
    }
}