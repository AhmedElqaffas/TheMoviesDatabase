package com.example.moviesretrofit.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataRepositories.FindMultiMediaRepository
import com.example.moviesretrofit.dataClasses.MultiMediaRepositoryResponse

class FindMultiMediaViewModel: ViewModel() {
    fun findMediaByName(page: Int, name: String, searchTextChanged: Boolean): LiveData<MultiMediaRepositoryResponse>{
        return FindMultiMediaRepository.findMediaByName(page, name, searchTextChanged)
    }

     fun removePeopleEntriesFromResponse(foundMediaList: List<MultiMedia>): MutableList<MultiMedia>{
        val entriesList = mutableListOf<MultiMedia>()
        for(entry in foundMediaList){
            if(entry.mediaType != "person"){
                entriesList.add(entry)
            }
        }
        return entriesList
    }
}