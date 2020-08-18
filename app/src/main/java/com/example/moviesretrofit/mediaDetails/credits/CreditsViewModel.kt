package com.example.moviesretrofit.mediaDetails.credits

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.dataClasses.Series
import com.example.moviesretrofit.helpers.MultiMediaCaster
import java.lang.Exception

class CreditsViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CreditsRepository.createDatabase(application)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        return CreditsRepository.getMultimediaCredits(MultiMediaCaster.castMultimedia(multimedia))
    }
}