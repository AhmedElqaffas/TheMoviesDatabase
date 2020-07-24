package com.example.moviesretrofit.mediaDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.CreditsResponse
import com.example.moviesretrofit.dataClasses.Person

class MediaDetailsViewModel: ViewModel() {

    fun getMultimediaCredits(id: Int, multimediaType: Int): LiveData<List<Person>>{
        return CastFragmentModel.getMultimediaCredits(id, multimediaType)
    }
}