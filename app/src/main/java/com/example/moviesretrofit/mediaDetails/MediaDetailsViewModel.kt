package com.example.moviesretrofit.mediaDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.CreditsResponse
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person

class MediaDetailsViewModel: ViewModel() {

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        return CastFragmentModel.getMultimediaCredits(multimedia)
    }
}