package com.example.moviesretrofit.helpers

import android.util.Log
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Series

object MultiMediaCaster {

     fun castMultimedia(multimedia: MultiMedia): MultiMedia {
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
        }catch (e: java.lang.Exception){
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
                multimedia.releaseDate,
                multimedia.mediaType,
                multimedia.overview,
                multimedia.popularity,
                budget,
                revenue,
                multimedia.genres,
                multimedia.isFavorite
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
                multimedia.genres,
                multimedia.isFavorite
            )

            else -> throw Exception("Couldn't cast multimedia in MultimediaDetails viewModel")
        }
    }
}