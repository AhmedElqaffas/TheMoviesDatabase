package com.example.moviesretrofit.mediaDetails

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Series
import com.example.moviesretrofit.mediaDetails.credits.CreditsRepository
import java.lang.Exception

class MultimediaDetailsViewModel(application: Application): AndroidViewModel(application) {


    init {
        MultimediaDetailsRepository.createDatabase(application)
    }


    fun determineProgressBarColor(rating: Int): ColorStateList{
        return when(rating){
            in 0 until 60 ->
                 ColorStateList.valueOf(Color.parseColor("#FF0000"))
            in 60 until 75 ->
                ColorStateList.valueOf(Color.parseColor("#FF9800"))
            else ->
                ColorStateList.valueOf(Color.parseColor("#00FF00"))
        }
    }

    fun getMultimediaDetails(multimedia: MultiMedia): LiveData<MultiMedia>{
        val castedMultimedia = castMultimedia(multimedia)
        return MultimediaDetailsRepository.getMultimediaDetails(castedMultimedia)
    }

    private fun castMultimedia(multimedia: MultiMedia): MultiMedia {
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
                multimedia.budget,
                multimedia.revenue
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
                multimedia.popularity
            )

            else -> throw Exception("Couldn't cast multimedia in MultimediaDetails viewModel")
        }
    }
}