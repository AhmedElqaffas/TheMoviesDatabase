package com.example.moviesretrofit.mediaDetails

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moviesretrofit.dataClasses.Movie
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.Series
import com.example.moviesretrofit.helpers.MultiMediaCaster
import kotlin.Exception

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
        return MultimediaDetailsRepository.getMultimediaDetails(MultiMediaCaster.castMultimedia(multimedia))
    }

    /**
     * Removes a multimedia from favorites if it is already in favorites
     * or add it if it is not
     */
    fun toggleFavorites(multimedia: MultiMedia){
        MultimediaDetailsRepository.toggleFavorites(MultiMediaCaster.castMultimedia(multimedia))
    }

}