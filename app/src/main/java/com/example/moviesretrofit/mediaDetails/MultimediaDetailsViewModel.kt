package com.example.moviesretrofit.mediaDetails

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.ViewModel

class MultimediaDetailsViewModel: ViewModel() {

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
}