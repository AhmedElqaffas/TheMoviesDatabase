package com.example.moviesretrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie(val title: String,
                 @SerializedName("poster_path") val poster: String,
                 @SerializedName("backdrop_path") val cover: String,
                 @SerializedName("vote_average") val rating: Float,
                 val overview: String): Serializable