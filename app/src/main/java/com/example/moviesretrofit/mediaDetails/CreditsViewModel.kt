package com.example.moviesretrofit.mediaDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person

class CreditsViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CastFragmentModel.createDatabase(application)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        return CastFragmentModel.getMultimediaCredits(multimedia)
    }
}