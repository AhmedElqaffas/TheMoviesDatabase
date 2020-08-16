package com.example.moviesretrofit.mediaDetails.credits

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Person
import com.example.moviesretrofit.dataClasses.Series
import java.lang.Exception

class CreditsViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CreditsRepository.createDatabase(application)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        val castedMultimedia = castMultimedia(multimedia)
        return CreditsRepository.getMultimediaCredits(castedMultimedia)
    }

    private fun castMultimedia(multimedia: MultiMedia): MultiMedia{
        var budget: Int? = 0
        var revenue: Long? = 0
        try{
            budget = (multimedia as Movie).budget
            revenue = multimedia.revenue
        }catch (e: Exception){
            Log.i("multimediaCasting", "Couldn't cast multimedia to movie")
        }

        var numberOfSeasons: Int? = null
        var lastAirDate =  ""
        var inProduction = false
        try{
            numberOfSeasons = (multimedia as Series).numberOfSeasons
            lastAirDate = multimedia.lastAirDate
            inProduction = multimedia.inProduction
        }catch (e: Exception){
            Log.i("multimediaCasting", "Couldn't cast multimedia to series")
        }
        return when (multimedia.mediaType) {
            "movie" -> Movie(
                multimedia.title,
                multimedia.id,
                multimedia.totalVotes,
                multimedia.poster,
                multimedia.cover,
                multimedia.rating,
                multimedia.releaseDate ,
                multimedia.mediaType,
                multimedia.overview,
                multimedia.popularity,
                budget,
                revenue,
                multimedia.genres
            )

            "tv" -> Series(
                multimedia.title,
                multimedia.id,
                multimedia.totalVotes,
                multimedia.poster,
                multimedia.cover,
                multimedia.rating,
                multimedia.releaseDate,
                multimedia.mediaType,
                multimedia.overview,
                multimedia.popularity,
                numberOfSeasons,
                lastAirDate,
                inProduction,
                multimedia.genres
            )

            else -> throw Exception("Couldn't cast multimedia in cast repo")
        }
    }

}