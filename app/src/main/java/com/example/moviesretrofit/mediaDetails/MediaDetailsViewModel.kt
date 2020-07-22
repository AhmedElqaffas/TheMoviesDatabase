package com.example.moviesretrofit.mediaDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.CreditsResponse
import com.example.moviesretrofit.dataClasses.Person

class MediaDetailsViewModel: ViewModel() {

    fun getMultimediaCredits(id: Int, multimediaType: Int): LiveData<CreditsResponse>{
        return CastFragmentModel.getMultimediaCredits(id, multimediaType)
    }

    fun appendCastAndCrewLists(creditsResponse: CreditsResponse): List<Person> {
        val combinedList = mutableListOf<Person>()
        combinedList.addAll(creditsResponse.cast)
        combinedList.addAll(creditsResponse.crew)
        return combinedList.toList()
    }
}