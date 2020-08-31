package com.example.tmdb.mediaDetails.credits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.dataClasses.Person
import com.example.tmdb.helpers.MultiMediaCaster

class CreditsViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CreditsRepository.createDatabase(application)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        return CreditsRepository.getMultimediaCredits(MultiMediaCaster.castMultimedia(multimedia))
    }
}